package net.mohron.skyclaims.island;

import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final DataStore GRIEF_PREVENTION_DATA = PLUGIN.getGriefPrevention().dataStore;

	private UUID owner;
	private Claim claim;
	private Location<World> spawn;
	private boolean isReady;
	private int radius;

	public Island(UUID owner, Claim claim) {
		this.owner = owner;
		this.claim = claim;
		this.isReady = false;

		IslandUtil.buildIsland(this);
	}

	public Island(UUID owner, UUID worldId, UUID claimId) {
		WorldProperties world = PLUGIN.getGame().getServer().getWorld(worldId).orElseGet(WorldUtil::getDefaultWorld).getProperties();
		this.claim = GRIEF_PREVENTION_DATA.getClaim(world, claimId);
		this.owner = owner;
		this.isReady = true;
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

	public Claim getParentClaim() {
		return claim.parent;
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

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		this.spawn = spawn;
	}

	public int getRadius() {
		return (claim.getLesserBoundaryCorner().getBlockX() - claim.getGreaterBoundaryCorner().getBlockX()) / 2;
	}

	public Location getCenter() {
		int radius = this.getRadius();
		return new Location<>(getSpawn().getExtent(), claim.getLesserBoundaryCorner().getX() + radius, 64, claim.getLesserBoundaryCorner().getZ() + radius);
	}

	public void toggleIsReady() {
		isReady = !isReady;
	}

	public boolean isReady() {
		return isReady;
	}
}