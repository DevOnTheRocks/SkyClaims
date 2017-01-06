package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.SkyClaim;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.util.WorldUtil;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());

	private UUID id;
	private UUID owner;
	private Claim claim;
	private Location<World> spawn;
	private boolean locked;

	public Island(Player owner, Claim claim, String schematic) {
		this.owner = owner.getUniqueId();
		this.claim = claim;
		this.spawn = getCenter();
		this.locked = false;

		GenerateIslandTask generateIsland = new GenerateIslandTask(owner, this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

		save();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Island(UUID id, UUID owner, UUID worldId, UUID claimId, Vector3i spawnLocation) {
		World world = PLUGIN.getGame().getServer().getWorld(worldId).orElseGet(WorldUtil::getDefaultWorld);

		this.id = UUID.randomUUID();
		this.owner = owner;
		this.spawn = new Location<>(world, spawnLocation);

		// 1st attempt to load claim by ID
		// 2nd attempt to find claim by location
		// Finally create a new claim after removing all overlapping claims if any
		if (CLAIM_MANAGER.getClaimByUUID(claimId).isPresent()) {
			this.claim = CLAIM_MANAGER.getClaimByUUID(claimId).get();
		} else if (CLAIM_MANAGER.getClaimAt(spawn, true).getType() == ClaimType.BASIC
				&& CLAIM_MANAGER.getClaimAt(spawn, true).getOwnerUniqueId().equals(owner)) {
			this.claim = CLAIM_MANAGER.getClaimAt(spawn, true);
		} else {
			ClaimResult claim;
			do {
				claim = ClaimUtil.createIslandClaim(getUser().get(), getRegion());
				switch (claim.getResultType()) {
					case SUCCESS:
						this.claim = claim.getClaim().get();
						break;
					case OVERLAPPING_CLAIM:
						CLAIM_MANAGER.deleteClaim(claim.getClaim().get(), Cause.source(PLUGIN).build());
						PLUGIN.getLogger().error(String.format("Removing overlapping claim (Owner: %s, ID: %s) while restoring %s's island.", claim.getClaim().get().getOwnerName(), claim.getClaim().get().getUniqueId(), getOwnerName()));
						break;
					default:
						PLUGIN.getLogger().error(String.format("Failed to create claim for %s's island, reason: %s", getOwnerName(), claim.getResultType()));
						break;
				}
			} while (claim.getResultType() == ClaimResultType.OVERLAPPING_CLAIM);
		}
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Island(UUID owner, UUID worldId, UUID claimId, Vector3i spawnLocation) {
		World world = PLUGIN.getGame().getServer().getWorld(worldId).orElseGet(WorldUtil::getDefaultWorld);

		this.owner = owner;
		this.spawn = new Location<>(world, spawnLocation);

		// 1st attempt to load claim by ID
		// 2nd attempt to find claim by location
		// Finally create a new claim after removing all overlapping claims if any
		if (CLAIM_MANAGER.getClaimByUUID(claimId).isPresent()) {
			this.claim = CLAIM_MANAGER.getClaimByUUID(claimId).get();
		} else if (CLAIM_MANAGER.getClaimAt(spawn, true).getType() == ClaimType.BASIC
				&& CLAIM_MANAGER.getClaimAt(spawn, true).getOwnerUniqueId().equals(owner)) {
			this.claim = CLAIM_MANAGER.getClaimAt(spawn, true);
		} else {
			ClaimResult claim;
			do {
				claim = ClaimUtil.createIslandClaim(getUser().get(), getRegion());
				switch (claim.getResultType()) {
					case SUCCESS:
						this.claim = claim.getClaim().get();
						break;
					case OVERLAPPING_CLAIM:
						CLAIM_MANAGER.deleteClaim(claim.getClaim().get(), Cause.source(PLUGIN).build());
						PLUGIN.getLogger().error(String.format("Removing overlapping claim (Owner: %s, ID: %s) while restoring %s's island.", claim.getClaim().get().getOwnerName(), claim.getClaim().get().getUniqueId(), getOwnerName()));
						break;
					default:
						PLUGIN.getLogger().error(String.format("Failed to create claim for %s's island, reason: %s", getOwnerName(), claim.getResultType()));
						break;
				}
			} while (claim.getResultType() == ClaimResultType.OVERLAPPING_CLAIM);
		}
	}

	public UUID getUniqueId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return (getUser().isPresent()) ? getUser().get().getName() : "Unknown";
	}

	public Optional<User> getUser() {
		Optional<UserStorageService> optStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		if (optStorage.isPresent()) {
			UserStorageService storage = optStorage.get();
			return (storage.get(owner).isPresent()) ? Optional.of(storage.get(owner).get()) : Optional.empty();
		}
		return Optional.empty();
	}

	public Claim getClaim() {
		return claim;
	}

	public String getDateCreated() {
		return claim.getClaimData().getDateCreated().toString();
	}

	public Text getName() {
		return (claim.getName().isPresent()) ? claim.getName().get() : Text.of(claim.getUniqueId().toString());
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		save();
	}

	public World getWorld() {
		return ConfigUtil.getWorld();
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		this.spawn = spawn;
		save();
	}

	public boolean isWithinIsland(Location<World> location) {
		return claim.contains(location, true, false);
	}

	public int getRadius() {
		return (claim.getGreaterBoundaryCorner().getBlockX() - claim.getLesserBoundaryCorner().getBlockX()) / 2;
	}

	public Location<World> getCenter() {
		return getRegion().getCenterBlock();
	}

	public boolean hasPermissions(Player player) {
		return player.getUniqueId().equals(claim.getOwnerUniqueId())
				|| claim.getTrustManager().getContainers().contains(player.getUniqueId())
				|| claim.getTrustManager().getBuilders().contains(player.getUniqueId())
				|| claim.getTrustManager().getManagers().contains(player.getUniqueId());
	}

	public Region getRegion() {
		return new Region(getSpawn().getChunkPosition().getX() >> 5, getSpawn().getChunkPosition().getZ() >> 5);
	}

	public void save() {
		HashMap<UUID, Island> island = new HashMap<>();
		island.put(id, this);

		if (SkyClaims.islands.containsKey(owner))
			SkyClaims.islands.put(owner, island);

		PLUGIN.getDatabase().saveIsland(this);
	}

	public void delete() {
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		SkyClaims.islands.remove(owner);
		PLUGIN.getDatabase().removeIsland(this);
	}
}