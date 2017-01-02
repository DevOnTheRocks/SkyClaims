package net.mohron.skyclaims;

public class Region {
	public int x;
	public int z;

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
}