package net.mohron.skyclaims;

import net.mohron.skyclaims.claim.SkyClaim;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.IslandUtil;

import java.util.ArrayList;

public class Region {
	private int x;
	private int z;

	public Region(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public Coordinate getLesserBoundary() {
		return new Coordinate(x << 5 << 4, z << 5 << 4);
	}

	public Coordinate getGreaterBoundary() {
		return new Coordinate((((x + 1) << 5) << 4) - 1, (((z + 1) << 5) << 4) - 1);
	}

	public static boolean isTaken(Region inputRegion) {
		ArrayList<Island> islands = new ArrayList<>(SkyClaims.islands.values());

		for (Island island : islands) {
			if (inputRegion.equals(island.getRegion()))
				return true;
		}

		return false;
	}
}