/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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

import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.server.ServerLocation;

public final class WorldUtil {

  private WorldUtil() {
  }

  public static void setBlockBiome(ServerLocation location, Biome biome) {
    location.world().setBiome(
        location.blockX(),
        0,
        location.blockZ(),
        biome);
  }

  public static void setChunkBiome(ServerLocation location, Biome biome) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        location.world().setBiome(
            location.chunkPosition().x() * 16 + x,
            0,
            location.chunkPosition().z() * 16 + z,
            biome
        );
      }
    }
  }

  public static void setIslandBiome(Island island, Biome biome) {
    if (island.getClaim().isPresent()) {
      int x1 = island.getClaim().get().getLesserBoundaryCorner().getX();
      int x2 = island.getClaim().get().getGreaterBoundaryCorner().getX();
      int z1 = island.getClaim().get().getLesserBoundaryCorner().getZ();
      int z2 = island.getClaim().get().getGreaterBoundaryCorner().getZ();
      for (int x = x1; x < x2; x++) {
        for (int z = z1; z < z2; z++) {
          island.getWorld().setBiome(
              x,
              0,
              z,
              biome
          );
        }
      }
    } else {
      setRegionBiome(island, biome);
    }
  }

  public static void setRegionBiome(Island island, Biome biome) {
    Region region = island.getRegion();
    for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x++) {
      for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z++) {
        island.getWorld().setBiome(x, 0, z, biome);
      }
    }
  }
}