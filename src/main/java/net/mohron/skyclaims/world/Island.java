/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final World WORLD = PLUGIN.getConfig().getWorldConfig().getWorld();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WORLD);
	private static final IRegionPattern PATTERN = new SpiralRegionPattern();

	private UUID id;
	private UUID owner;
	private UUID claim;
	private Transform<World> spawn;
	private boolean locked;

	public Island(User owner, String schematic) throws CreateIslandException {
		this.id = UUID.randomUUID();

		this.owner = owner.getUniqueId();
		Region region;
		try {
			region = PATTERN.nextRegion();
		} catch (InvalidRegionException e) {
			throw new CreateIslandException(e.getText());
		}
		this.spawn = new Transform<>(region.getCenter());
		this.locked = true;

		// Create the island claim
		this.claim = ClaimUtil.createIslandClaim(owner.getUniqueId(), region).getUniqueId();

		// Run commands defined in config on creation
		for (String command : PLUGIN.getConfig().getMiscConfig().getCreateCommands()) {
			PLUGIN.getGame().getCommandManager()
				.process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", owner.getName()));
		}

		// Generate the island using the specified schematic
		GenerateIslandTask generateIsland = new GenerateIslandTask(owner.getUniqueId(), this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

		save();
	}

	public Island(UUID id, UUID owner, UUID claimId, Vector3d spawnLocation, boolean locked) {
		this.id = id;
		this.owner = owner;
		this.spawn = new Transform<>(WORLD, spawnLocation);
		this.locked = locked;
		this.claim = claimId;

		Claim claim = CLAIM_MANAGER.getClaimByUUID(claimId).orElse(null);
		if (claim != null) {
			this.claim = claimId;
			int initialWidth = Options.getIntOption(owner, Options.INITIAL_SIZE, 8, 256) * 2;
			// Resize claims smaller than the player's initial-size
			if (claim.getWidth() < initialWidth)
				setWidth(initialWidth);
			claim.getData().setResizable(false);
			claim.getData().setClaimExpiration(false);
			claim.getData().setRequiresClaimBlocks(false);
		} else {
			try {
				this.claim = ClaimUtil.createIslandClaim(owner, getRegion()).getUniqueId();
				PLUGIN.queueForSaving(this);
			} catch (CreateIslandException e) {
				PLUGIN.getLogger().error("Failed to create a new claim for island " + id);
			}
		}
	}

	public static Optional<Island> get(UUID islandUniqueId) {
		return SkyClaims.islands.entrySet().stream()
			.filter(i -> i.getValue().getUniqueId().equals(islandUniqueId))
			.map(Map.Entry::getValue)
			.findFirst();
	}

	public static Optional<Island> get(Location<World> location) {
		return SkyClaims.islands.entrySet().stream()
			.filter(i -> i.getValue().contains(location))
			.map(Map.Entry::getValue)
			.findFirst();
	}

	public static Optional<Island> get(Claim claim) {
		for (Island island : SkyClaims.islands.values()) {
			if (island.getClaim().isPresent() && island.getClaim().get().equals(claim)) return Optional.of(island);
		}
		return Optional.empty();
	}

	@Deprecated
	public static Optional<Island> getByOwner(UUID owner) {
		for (Island island : SkyClaims.islands.values()) {
			if (island.getOwnerUniqueId().equals(owner)) return Optional.of(island);
		}
		return Optional.empty();
	}

	public static boolean hasIsland(UUID owner) {
		if (SkyClaims.islands.isEmpty()) return false;
		for (Island island : SkyClaims.islands.values()) {
			if (island.getOwnerUniqueId().equals(owner)) return true;
		}
		return false;
	}

	public static int getIslandsOwned(UUID owner) {
		return (int) SkyClaims.islands.entrySet().stream()
			.filter(i -> i.getValue().getOwnerUniqueId().equals(owner))
			.count();
	}

	public UUID getUniqueId() {
		return id;
	}

	public UUID getOwnerUniqueId() {
		return owner;
	}

	public String getOwnerName() {
		return getName(owner);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private String getName(UUID uuid) {
		Optional<User> player = PLUGIN.getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(uuid);
		if (player.isPresent()) {
			return player.get().getName();
		} else {
			try {
				return PLUGIN.getGame().getServer().getGameProfileManager().get(uuid).get().getName().get();
			} catch (Exception e) {
				return "somebody";
			}
		}
	}

	public UUID getClaimUniqueId() {
		return claim;
	}

	public Optional<Claim> getClaim() {
		return CLAIM_MANAGER.getClaimByUUID(this.claim);
	}

	public Date getDateCreated() {
		return (getClaim().isPresent()) ? Date.from(getClaim().get().getData().getDateCreated()) : null;
	}

	public Text getName() {
		return (getClaim().isPresent() && getClaim().get().getName().isPresent()) ?
			getClaim().get().getName().get() :
			Text.of(TextColors.AQUA, getOwnerName(), "'s Island");
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		// TODO: Set protection flags if possible
		save();
	}

	public World getWorld() {
		return WORLD;
	}

	public Transform<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Transform<World> transform) {
		if (contains(transform.getLocation())) {
			Transform<World> spawn = new Transform<>(WORLD, transform.getPosition(), transform.getRotation());
			if (transform.getLocation().getY() < 0 || transform.getLocation().getY() > 255) {
				spawn.setPosition(
					new Vector3d(spawn.getLocation().getX(), PLUGIN.getConfig().getWorldConfig().getDefaultHeight(),
						spawn.getLocation().getZ()
					));
			}
			this.spawn = spawn;
			getClaim().ifPresent(claim -> claim.getData().setSpawnPos(spawn.getPosition().toInt()));
			save();
		}
	}

	private boolean contains(Location<World> location) {
		if (!getClaim().isPresent())
			return location.getExtent().equals(getWorld()) && Region.get(location).equals(getRegion());

		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		Location lesserBoundaryCorner = getClaim().get().getLesserBoundaryCorner();
		Location greaterBoundaryCorner = getClaim().get().getGreaterBoundaryCorner();

		return x >= lesserBoundaryCorner.getBlockX()
			&& x <= greaterBoundaryCorner.getBlockX()
			&& y >= lesserBoundaryCorner.getBlockY()
			&& y <= greaterBoundaryCorner.getBlockY()
			&& z >= lesserBoundaryCorner.getBlockZ()
			&& z <= greaterBoundaryCorner.getBlockZ();
	}

	public int getWidth() {
		return getClaim().isPresent() ? getClaim().get().getWidth() : 512;
	}

	private boolean setWidth(int width) {
		if (width < 0 || width > 512) return false;
		getClaim().ifPresent(claim -> {
			int spacing = (512 - width) / 2;
			claim.resize(
				new Vector3i(getRegion().getLesserBoundary().getX() + spacing, 0, getRegion().getLesserBoundary().getZ() + spacing),
				new Vector3i(getRegion().getGreaterBoundary().getX() - spacing, 255, getRegion().getGreaterBoundary().getZ() - spacing),
				PLUGIN.getCause()
			);
		});
		return getWidth() == width;
	}

	public Set<String> getMembers() {
		Set<String> members = Sets.newHashSet();
		if (!getClaim().isPresent()) return members;
		for (UUID builder : getClaim().get().getTrusts(TrustType.BUILDER)) {
			members.add(getName(builder));
		}
		for (UUID manager : getClaim().get().getTrusts(TrustType.MANAGER)) {
			members.add(getName(manager));
		}
		return members;
	}

	public boolean hasPermissions(User user) {
		return user.getUniqueId().equals(owner)
			|| (getClaim().isPresent() && getClaim().get().getAllTrusts().contains(user.getUniqueId()));
	}

	public Set<Player> getPlayers() {
		return PLUGIN.getGame().getServer().getOnlinePlayers().stream()
			.filter(p -> contains(p.getLocation()))
			.collect(Collectors.toSet());
	}

	public Region getRegion() {
		return Region.get(getSpawn().getLocation());
	}

	public void transfer(User user) {
		getClaim().ifPresent(claim -> {
			ClaimResult result = claim.transferOwner(user.getUniqueId());
			if (result.getResultType() != ClaimResultType.SUCCESS) {
				PLUGIN.getLogger().error(
					String.format("Failed to transfer claim (%s) when transferring %s's island to %s.\n%s",
						claim.getUniqueId(), getOwnerName(), user.getName(), result.getResultType()
					));
			}
		});
		this.owner = user.getUniqueId();
		save();
	}

	public void expand(int blocks) {
		if (blocks < 1) return;
		setWidth(getWidth() + blocks * 2);
	}

	private void save() {
		SkyClaims.islands.put(id, this);
		PLUGIN.getDatabase().saveIsland(this);
	}

	public void clear() {
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
	}

	public void regen(String schematic) {
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
	}

	public void delete() {
		getClaim().ifPresent(claim -> CLAIM_MANAGER.deleteClaim(claim, PLUGIN.getCause()));
		SkyClaims.islands.remove(id);
		PLUGIN.getDatabase().removeIsland(this);
	}
}