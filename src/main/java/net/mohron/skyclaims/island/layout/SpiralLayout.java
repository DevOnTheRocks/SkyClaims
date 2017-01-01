package net.mohron.skyclaims.island.layout;


import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.ConfigUtil;

import java.awt.*;
import java.util.ArrayList;

public class SpiralLayout implements ILayout {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static GlobalConfig config = PLUGIN.getConfig();
	private static final int spawnRegions = ConfigUtil.get(config.world.spawnRegions, 1);

	/**
	 * A method to generate a region-scaled spiral pattern and return the x/y pairs of each region
	 *
	 * @return An ArrayList of Points containing the x,y of regions, representing a spiral shape
	 */
	private static ArrayList<Point> generateRegionSpiral() {
		int islandCount = SkyClaims.islands.size();
		int generationSize = (int) Math.sqrt((double) islandCount + spawnRegions) + 1;

		ArrayList<Point> coordinates = new ArrayList<>(generationSize);
		int[] delta = {0, -1};
		int x = 0;
		int y = 0;

		for (int i = (int) Math.pow(Math.max(generationSize, generationSize), 2); i > 0; i--) {
			if ((-generationSize / 2 < x && x <= generationSize / 2) && (-generationSize / 2 < y && y <= generationSize / 2))
				coordinates.add(new Point(x, y));
			if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
				// change direction
				int a = delta[0];
				delta[0] = -delta[1];
				delta[1] = a;
			}
			PLUGIN.getLogger().info(x + ", " + y);
			x += delta[0];
			y += delta[1];
		}

		return coordinates;
	}

	public Point nextRegion() {
		ArrayList<Point> regions = generateRegionSpiral();
		int i = 0;

		PLUGIN.getLogger().info(String.format("Checking for next region out of %s points.", regions.size()));
		for (Point point : regions) {
			if (i < spawnRegions) {
				PLUGIN.getLogger().info(String.format("Skipping (%s, %s) for spawn", point.getX(), point.getY()));
				i++;
				continue;
			}
			PLUGIN.getLogger().info(String.format("Checking region (%s, %s) for island", point.getX(), point.getY()));
			if (SkyClaims.islands.isEmpty()) return point;
			for (Island island : SkyClaims.islands.values()) {
				PLUGIN.getLogger().info(String.format("Checking island %s (%s,%s) against region (%s, %s)", island.getOwnerName(), island.getRegionX(), island.getRegionZ(), point.getX(), point.getY()));
				if (point.x == island.getRegionX() && point.y == island.getRegionZ()) continue;
				PLUGIN.getLogger().info(String.format("Next Available Region is (%s, %s)", point.getX(), point.getY()));
				return point;
			}
		}

		return regions.get(regions.size() - 1);
	}
}
