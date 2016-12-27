package net.mohron.skyclaims.island;

import me.lucko.luckperms.api.LuckPermsApi;
import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.IslandUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

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

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
		if (PLUGIN.getLuckPerms().isPresent()) {
			LuckPermsApi api = PLUGIN.getLuckPerms().get();
			return api.getUser(owner).getName();
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