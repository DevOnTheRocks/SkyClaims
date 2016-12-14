package net.mohron.skyclaims;

import java.util.UUID;

public class IslandTasks {
	public static Island createIsland(UUID owner) {
		int x, z;

		//TODO designate the location of the island to be created

		buildIsland(owner);

		return new Island(owner);
	}

	public static void resetIsland(UUID owner) {
		clearIsland(owner);
		buildIsland(owner);
	}

	private static void buildIsland(UUID owner) {
		//TODO Build an "island" in the center of the owners island using a schematic
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}

}
