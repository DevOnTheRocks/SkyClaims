package net.mohron.skyclaims;

import me.ryanhamshire.griefprevention.claim.Claim;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Island {
	private UUID owner;
	private Claim claim;
	private Location<World> spawn;
	private boolean isReady;
	private int radius;

	public Island(UUID owner) {
		this.owner = owner;
		this.isReady = false;
	}

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
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
		return new Location(getSpawn().getExtent(), claim.getLesserBoundaryCorner().getX() + radius, 64, claim.getLesserBoundaryCorner().getZ() + radius);
	}

	public boolean isReady() {
		return isReady;
	}
}