package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class IslandUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());

	public static boolean hasIsland(UUID owner) {
		for (Island island : SkyClaims.islands.values()) {
			if (island.getOwnerUniqueId().equals(owner)) return true;
		}
		return false;
	}

	public static int getIslandsOwned(UUID owner) {
		int i = 0;
		for (Island island : SkyClaims.islands.values()) {
			if (island.getOwnerUniqueId().equals(owner)) i++;
		}
		return i;
	}

	public static Optional<Island> getIsland(UUID islandUniqueId) {
		return (SkyClaims.islands.containsKey(islandUniqueId)) ? Optional.of(SkyClaims.islands.get(islandUniqueId)) : Optional.empty();
	}

	@Deprecated
	public static Optional<Island> getIslandByOwner(UUID owner) {
		for (Island island : SkyClaims.islands.values()) {
			if (island.getOwnerUniqueId().equals(owner)) return Optional.of(island);
		}
		return Optional.empty();
	}

	public static Optional<Island> getIslandByLocation(Location<World> location) {
		return getIslandByClaim(CLAIM_MANAGER.getClaimAt(location, true));
	}

	private static Optional<Island> getIslandByClaim(Claim claim) {
		if (claim.getOwnerUniqueId() != null)
			for (Island island : SkyClaims.islands.values())
				if (island.getClaim().equals(claim)) return Optional.of(island);
		return Optional.empty();
	}
}