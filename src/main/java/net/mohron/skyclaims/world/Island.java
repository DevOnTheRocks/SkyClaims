package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(ConfigUtil.getWorld());
	private static final IRegionPattern PATTERN = new SpiralRegionPattern();

	private UUID id;
	private UUID owner;
	private UUID claim;
	private Location<World> spawn;
	private boolean locked;
	private Claim workingClaim;

	public Island(User owner, String schematic) throws CreateIslandException {
		this.id = UUID.randomUUID();

		this.owner = owner.getUniqueId();
		Region region;
		try {
			region = PATTERN.nextRegion();
		} catch (InvalidRegionException e) {
			throw new CreateIslandException(e.getText());
		}
		this.spawn = region.getCenterBlock();
		PLUGIN.getLogger().info(String.format("Set Island spawn to %s", region.getCenterBlock().toString()));
		this.locked = false;

		// Create the island claim
		this.claim = ClaimUtil.createIslandClaim(owner.getUniqueId(), region).getUniqueId();

		// Run commands defined in config on creation
		ConfigUtil.getCreateCommands().ifPresent(commands -> {
			for (String command : commands) {
				PLUGIN.getGame().getCommandManager().process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", owner.getName()));
			}
		});

		// Generate the island using the specified schematic
		GenerateIslandTask generateIsland = new GenerateIslandTask(owner.getUniqueId(), this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

		save();
	}

	public Island(UUID id, UUID owner, UUID claimId, Vector3i spawnLocation, boolean locked) {
		this.id = id;
		this.owner = owner;
		this.spawn = new Location<>(ConfigUtil.getWorld(), spawnLocation);
		this.locked = locked;
		this.claim = claimId;
		// 1st attempt to load claim by ID
		// 2nd attempt to find claim by location
		// Finally create a new claim after removing all overlapping claims if any

		if (CLAIM_MANAGER.getClaimByUUID(claimId).isPresent()) {
			this.claim = claimId;
		} else {
			try {
				this.claim = ClaimUtil.createIslandClaim(owner, getRegion()).getUniqueId();
				//Send to Save Queue
				//PLUGIN.queueForSaving(this);
				delete();

			} catch (CreateIslandException e) {
				//PLUGIN.getLogger().error("Failed to create a new claim for island " + id);
			}
		}
	}

	public UUID getUniqueId() {
		return id;
	}

	public UUID getOwnerUniqueId() {
		return owner;
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public String getOwnerName() {
		Optional<User> player = PLUGIN.getGame().getServiceManager().provide(UserStorageService.class).get().get(owner);
		if (player.isPresent()) {
			return player.get().getName();
		} else {
			try {
				return PLUGIN.getGame().getServer().getGameProfileManager().get(owner).get().getName().get();
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
		return (getClaim().isPresent()) ? Date.from(getClaim().get().getClaimData().getDateCreated()) : null;
	}

	public Text getName() {
		return (getClaim().isPresent()) ? (getClaim().get().getName().isPresent()) ? getClaim().get().getName().get() : Text.of(getOwnerName(), "'s Island") : Text.of(getOwnerName(), "'s Island");
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		// Set protection flags if possible
		save();
	}

	public World getWorld() {
		return ConfigUtil.getWorld();
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		if (isWithinIsland(spawn)) {
			if (spawn.getY() < 0 || spawn.getY() > 255) {
				spawn = new Location<>(spawn.getExtent(), spawn.getX(), ConfigUtil.getIslandHeight(), spawn.getZ());
			}
			this.spawn = spawn;
			save();
		}
	}

	private boolean isWithinIsland(Location<World> location) {
		return location.getChunkPosition().getX() >> 5 == getRegion().getX() && location.getChunkPosition().getZ() >> 5 == getRegion().getZ();
	}

	public int getRadius() {
		return (getClaim().isPresent()) ? (getClaim().get().getGreaterBoundaryCorner().getBlockX() - getClaim().get().getLesserBoundaryCorner().getBlockX()) / 2 : 256;
	}

	public Set<UUID> getMembers() {
		Set<UUID> members = Sets.newHashSet();
		if (!getClaim().isPresent()) return members;
		for (UUID member : getClaim().get().getTrustManager().getBuilders()) {
			members.add(member);
		}
		for (UUID member : getClaim().get().getTrustManager().getManagers()) {
			members.add(member);
		}
		return members;
	}

	public boolean hasPermissions(Player player) {
		return player.getUniqueId().equals(owner)
				|| getClaim().isPresent()
				&& (getClaim().get().getTrustManager().getContainers().contains(player.getUniqueId())
				|| getClaim().get().getTrustManager().getBuilders().contains(player.getUniqueId())
				|| getClaim().get().getTrustManager().getManagers().contains(player.getUniqueId()));
	}

	public Set<Player> getPlayers() {
		Set<Player> players = Sets.newHashSet();
		for (Player player : PLUGIN.getGame().getServer().getOnlinePlayers()) {
			if (player.getLocation().getChunkPosition().getX() >> 5 == getRegion().getX()
					&& player.getLocation().getChunkPosition().getZ() >> 5 == getRegion().getZ())
				players.add(player);
		}
		return players;
	}

	public Region getRegion() {
		return new Region(getSpawn().getChunkPosition().getX() >> 5, getSpawn().getChunkPosition().getZ() >> 5);
	}

	public void save() {
		SkyClaims.islands.put(id, this);
		PLUGIN.getDatabase().saveIsland(this);
	}

	public void delete() {
		getClaim().ifPresent(claim -> {
			CLAIM_MANAGER.deleteClaim(claim, Cause.source(PLUGIN).build());
			SkyClaims.islandClaims.remove(claim);
		});
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		SkyClaims.islands.remove(id);
		PLUGIN.getDatabase().removeIsland(this);
	}
}