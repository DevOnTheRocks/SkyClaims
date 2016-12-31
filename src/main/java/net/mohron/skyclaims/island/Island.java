package net.mohron.skyclaims.island;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.DataStore;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.ClaimSystemFactory;
import net.mohron.skyclaims.claim.IClaim;
import net.mohron.skyclaims.claim.IClaimSystem;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final DataStore GRIEF_PREVENTION_DATA = PLUGIN.getGriefPrevention().dataStore;
	private static IClaimSystem claimSystem = ClaimSystemFactory.getClaimSystem();

	private UUID owner;
	private IClaim claim;
	private Location<World> spawn;

	public Island(UUID owner, IClaim claim, File schematic) {
		this.owner = owner;
		this.claim = claim;

		GenerateIslandTask generateIsland = new GenerateIslandTask(this, schematic);
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
		if (PLUGIN.getLuckPerms() != null) {
			return PLUGIN.getLuckPerms().getUser(owner).getName();
		} else {
			GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();

			try {
				return profileManager.get(owner).get().getName().orElse("Unknown");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return "Unknown";
		}
	}

	public IClaim getClaim() {
		return claim;
	}

	public UUID getClaimId() {
		return claim.getID();
	}

	public String getDateCreated() {
		return claim.getClaimData().getDateCreated();
	}

	public World getWorld() {
		return spawn.getExtent();
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		this.spawn = spawn;
	}

	public boolean isWithinIsland(Location<World> location) {
		return claim.contains(location, true, false);
	}

	public int getRadius() {
		return (claim.getLesserBoundaryCorner().getBlockX() - claim.getGreaterBoundaryCorner().getBlockX()) / 2;
	}

	public Location getCenter() {
		int radius = this.getRadius();
		return new Location<>(getSpawn().getExtent(), claim.getLesserBoundaryCorner().getX() + radius, 64, claim.getLesserBoundaryCorner().getZ() + radius);
	}
}