/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	private static final World WORLD = PLUGIN.getConfig().getWorldConfig().getWorld();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WORLD);

	public static boolean hasIsland(UUID owner) {
		if (SkyClaims.islands.isEmpty()) return false;
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