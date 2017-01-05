package net.mohron.skyclaims;

import net.mohron.skyclaims.island.Island;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Slind on 1/5/2017.
 */
public class IslandStore {

    private static Map<UUID, Island> islands = new HashMap<>();
    private static HashSet<Region> occupiedRegions = new HashSet<>();

    public static void overwriteIslands(Map<UUID, Island> uuidIslandMap) {
        islands = uuidIslandMap;
        occupiedRegions.clear();
        for (Island island : uuidIslandMap.values()) {
            occupiedRegions.add(island.getRegion());
        }
    }

    public static void addIsland(Island island) {
        islands.put(island.getOwner(), island);
        occupiedRegions.add(island.getRegion());
    }

    public static void removeIsland(Island island) {
        islands.remove(island.getOwner());
        occupiedRegions.remove(island.getRegion());
    }

    public static void emptyIslands() {
        islands.clear();
        occupiedRegions.clear();
    }

    public static Map<UUID, Island> getIslands() {
        return islands;
    }

    public static HashSet<Region> getOccupiedRegions() {
        return occupiedRegions;
    }
}
