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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

public class WorldUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static World getDefaultWorld() {
		String defaultWorldName = PLUGIN.getGame().getServer().getDefaultWorldName();
		return PLUGIN.getGame().getServer().getWorld(defaultWorldName).get();
	}

	public static void setBlockBiome(Location<World> location, BiomeType biomeType) {
		location.getExtent().setBiome(
			location.getBlockX(),
			0,
			location.getBlockZ(),
			biomeType);
	}

	public static void setChunkBiome(Location<World> location, BiomeType biomeType) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				location.getExtent().setBiome(
					location.getChunkPosition().getX() * 16 + x,
					0,
					location.getChunkPosition().getZ() * 16 + z,
					biomeType
				);
			}
		}
	}

	public static void setIslandBiome(Island island, BiomeType biomeType) {
		if (island.getClaim().isPresent()) {
			int x1 = island.getClaim().get().getLesserBoundaryCorner().getBlockX();
			int x2 = island.getClaim().get().getGreaterBoundaryCorner().getBlockX();
			int z1 = island.getClaim().get().getLesserBoundaryCorner().getBlockZ();
			int z2 = island.getClaim().get().getGreaterBoundaryCorner().getBlockZ();
			for (int x = x1; x < x2; x++) {
				for (int z = z1; z < z2; z++) {
					island.getWorld().setBiome(
						x,
						0,
						z,
						biomeType
					);
				}
			}
		} else setRegionBiome(island, biomeType);
	}

	public static void setRegionBiome(Island island, BiomeType biomeType) {
		Region region = island.getRegion();
		for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x++) {
			for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z++) {
				island.getWorld().setBiome(x, 0, z, biomeType);
			}
		}
	}
}