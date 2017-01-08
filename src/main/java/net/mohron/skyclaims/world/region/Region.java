package net.mohron.skyclaims.world.region;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.Coordinate;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

	public Location<World> getCenterBlock() {
		return new Location<>(
				ConfigUtil.getWorld(),
				(getGreaterBoundary().getX() + getLesserBoundary().getX()) / 2,
				ConfigUtil.getIslandHeight(),
				(getGreaterBoundary().getZ() + getLesserBoundary().getZ()) / 2
		);
	}

	public static boolean isOccupied(Region region) {
		if (SkyClaims.islands.isEmpty()) return false;

		for (Island island : SkyClaims.islands.values()) {
			if (region.equals(island.getRegion()))
				return true;
		}

		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Region region = (Region) o;

		return x == region.x && z == region.z;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + z;
		return result;
	}
}