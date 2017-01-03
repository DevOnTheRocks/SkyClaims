package net.mohron.skyclaims;

import net.mohron.skyclaims.island.Island;

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
			try {
				if (!inputRegion.equals(island.getRegion())) {
                    return false;
                }
			} catch (NullPointerException e) {
				SkyClaims.getInstance().getLogger().error("Could not get region of island " +  island.getOwner() + " " +
						island.getOwnerName() + " probably caused by broken claim relation.");
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Region region = (Region) o;

		if (x != region.x) return false;
		return z == region.z;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + z;
		return result;
	}
}