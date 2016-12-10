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

	public Island(UUID owner) {
		this.owner = owner;
		this.isReady = false;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
		GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();
		try {
			return profileManager.get(owner).get().getName().orElse("Unknown");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "Unknown";
	}

	public boolean isReady() {
		return isReady;
	}
}