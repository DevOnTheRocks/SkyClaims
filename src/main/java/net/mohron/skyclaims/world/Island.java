package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import net.mohron.skyclaims.SkyClaims;
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

import java.util.Optional;
import java.util.UUID;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());

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
	public Island(UUID owner, UUID worldId, UUID claimId, Vector3i spawnLocation) {
		World world = PLUGIN.getGame().getServer().getWorld(worldId).orElseGet(WorldUtil::getDefaultWorld);

		this.owner = owner;
		this.spawn = new Location<>(world, spawnLocation);

		Claim claim = CLAIM_MANAGER.getClaimByUUID(claimId)
				.orElse(ClaimUtil.createIslandClaim(getUser().get(), getRegion()).getClaim().get());
		if (!CLAIM_MANAGER.getClaimByUUID(claim.getUniqueId()).isPresent())
			CLAIM_MANAGER.addClaim(claim, Cause.source(PLUGIN).build());
		this.claim = claim;
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

	public UUID getClaimId() {
		return claim.getUniqueId();
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
		return player.getUniqueId().equals(claim.getOwnerUniqueId()) ||
				claim.getTrustManager().getContainers().contains(player.getUniqueId()) ||
				claim.getTrustManager().getBuilders().contains(player.getUniqueId()) ||
				claim.getTrustManager().getManagers().contains(player.getUniqueId());
	}

	public Region getRegion() {
		return new Region(getSpawn().getChunkPosition().getX() >> 5, getSpawn().getChunkPosition().getZ() >> 5);
	}

	public void save() {
		SkyClaims.islands.put(owner, this);
		PLUGIN.getDatabase().saveIsland(this);
	}

	public void delete() {
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		SkyClaims.islands.remove(owner);
		PLUGIN.getDatabase().removeIsland(this);
	}
}