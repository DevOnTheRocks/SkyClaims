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

package net.mohron.skyclaims.world.region;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.util.ClaimUtil;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class SpiralRegionPattern implements IRegionPattern {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static int spawnRegions;

	/**
	 * A method to generate a region-scaled spiral region and return the x/y pairs of each region
	 *
	 * @return An ArrayList of Points containing the x,y of regions, representing a spiral shape
	 */
	public ArrayList<Region> generateRegionPattern() {
		spawnRegions = PLUGIN.getConfig().getWorldConfig().getSpawnRegions();
		int islandCount = SkyClaims.islands.size();
		int generationSize = (int) Math.sqrt((double) islandCount + spawnRegions) + 1;
		String log = "Region Pattern: [";

		ArrayList<Region> coordinates = new ArrayList<>(generationSize);
		int[] delta = {0, -1};
		int x = 0;
		int y = 0;

		for (int i = (int) Math.pow(Math.max(generationSize, generationSize), 2); i > 0; i--) {
			if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
				// change direction
				int a = delta[0];
				delta[0] = -delta[1];
				delta[1] = a;
			}
			coordinates.add(new Region(x, y));
			log += String.format("(%s,%s),", x, y);
			x += delta[0];
			y += delta[1];
		}
		PLUGIN.getLogger().debug(log + "]");
		PLUGIN.getLogger().debug(String.format("Coordinates length: %s", coordinates.size()));
		return coordinates;
	}

	public Region nextRegion() throws InvalidRegionException {
		spawnRegions = PLUGIN.getConfig().getWorldConfig().getSpawnRegions();
		ArrayList<Region> spawnRegions = new ArrayList<>(SpiralRegionPattern.spawnRegions);
		ArrayList<Region> regions = generateRegionPattern();
		int iterator = 0;

		PLUGIN.getLogger().debug(String.format("Checking for next region out of %s points with %s spawn regions.", regions.size(), SpiralRegionPattern.spawnRegions));

		for (Region region : regions) {
			if (iterator < SpiralRegionPattern.spawnRegions) {
				spawnRegions.add(region);
				PLUGIN.getLogger().debug(String.format("Skipping (%s, %s) for spawn", region.getX(), region.getZ()));
				iterator++;
				continue;
			} else if (SkyClaims.islands.isEmpty())
				ClaimUtil.createSpawnClaim(spawnRegions);

			PLUGIN.getLogger().debug(String.format("Checking region (%s, %s) for island", region.getX(), region.getZ()));

			if (!Region.isOccupied(region)) {
				return region;
			}
		}

		throw new InvalidRegionException(Text.of("Failed to find a valid region!"));
	}
}