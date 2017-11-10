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

package net.mohron.skyclaims.world;

import com.google.common.collect.Maps;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class IslandManager {

    public final static Map<UUID, Island> ISLANDS = Maps.newHashMap();

    public Map<UUID, Island> getIslands() {
        return ISLANDS;
    }

    public static Optional<Island> get(UUID islandUniqueId) {
        return Optional.ofNullable(ISLANDS.get(islandUniqueId));
    }

    public static Optional<Island> get(Location<World> location) {
        return ISLANDS.values().stream()
            .filter(i -> i.contains(location))
            .findFirst();
    }

    public static Optional<Island> get(Claim claim) {
        for (Island island : ISLANDS.values()) {
            if (island.getClaim().isPresent() && island.getClaim().get().equals(claim)) {
                return Optional.of(island);
            }
        }
        return Optional.empty();
    }

    @Deprecated
    public static Optional<Island> getByOwner(UUID owner) {
        for (Island island : ISLANDS.values()) {
            if (island.getOwnerUniqueId().equals(owner)) {
                return Optional.of(island);
            }
        }
        return Optional.empty();
    }

    public static boolean hasIsland(UUID owner) {
        if (ISLANDS.isEmpty()) {
            return false;
        }
        for (Island island : ISLANDS.values()) {
            if (island.getOwnerUniqueId().equals(owner)) {
                return true;
            }
        }
        return false;
    }

    public static int getIslandsOwned(UUID owner) {
        return (int) ISLANDS.values().stream()
            .filter(i -> i.getOwnerUniqueId().equals(owner))
            .count();
    }
}
