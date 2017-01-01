package net.mohron.skyclaims.island;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static GlobalConfig config = PLUGIN.getConfig();
	private static final DataStore claimSystem = PLUGIN.getGriefPrevention().dataStore;
//	private static IClaimSystem claimSystem = ClaimSystemFactory.getClaimSystem();

	private UUID owner;
	private Claim claim;
	private Location<World> spawn;

	public Island(Player owner, Claim claim, String schematic) {
		this.owner = owner.getUniqueId();
		this.claim = claim;
		this.spawn = getCenter();

		GenerateIslandTask generateIsland = new GenerateIslandTask(owner, this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

		IslandUtil.saveIsland(this);
	}

	public Island(UUID owner, UUID worldId, UUID claimId, Vector3i spawnLocation) {
		World world = PLUGIN.getGame().getServer().getWorld(worldId).orElseGet(WorldUtil::getDefaultWorld);

		this.owner = owner;
		this.claim = claimSystem.getClaim(world.getProperties(), claimId);
		this.spawn = new Location<>(world, spawnLocation);
	}

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
		String name;
		if (PLUGIN.getLuckPerms() != null) {
			name = PLUGIN.getLuckPerms().getUser(owner).getName();
			if (name != null) return name;
		}

		name = getClaim().getOwnerName();
		if (name != null) return name;

		GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();

		try {
			return profileManager.get(owner).get().getName().orElse("Unknown");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return "Unknown";

	}

	public Claim getClaim() {
		return claim;
	}

	public UUID getClaimId() {
		return claim.getID();
	}

	public String getDateCreated() {
		return claim.getClaimData().getDateCreated();
	}

	public World getWorld() {
		return ConfigUtil.getWorld();
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		this.spawn = spawn;
		IslandUtil.saveIsland(this);
	}

	public boolean isWithinIsland(Location<World> location) {
		return claim.contains(location, true, false);
	}

	public int getRadius() {
		return (claim.getGreaterBoundaryCorner().getBlockX() - claim.getLesserBoundaryCorner().getBlockX()) / 2;
	}

	public Location<World> getCenter() {
		int radius = this.getRadius();
		return new Location<>(ConfigUtil.getWorld(),
				claim.getLesserBoundaryCorner().getX() + radius,
				ConfigUtil.get(config.world.defaultHeight, 64),
				claim.getLesserBoundaryCorner().getZ() + radius);
	}

	public boolean hasPermissions(Player player) {
		return player.getUniqueId().equals(claim.getOwnerUniqueId()) ||
				!claim.getClaimData().getContainers().contains(player.getUniqueId()) ||
				!claim.getClaimData().getBuilders().contains(player.getUniqueId()) ||
				!claim.getClaimData().getManagers().contains(player.getUniqueId());
	}

	public int getRegionX() {
		return getCenter().getBlockX() / 512;
	}

	public int getRegionZ() {
		return getCenter().getBlockZ() / 512;
	}
}