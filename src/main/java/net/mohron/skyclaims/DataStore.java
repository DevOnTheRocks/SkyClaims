package net.mohron.skyclaims;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStore {
	protected Map<UUID, Island> data = new HashMap<>();

	public DataStore(Map<UUID, Island> data) {
		this.data = data;
	}

	public void saveIsland(Island island) {
		data.put(island.getOwner(),island);
	}

	public Island createIsland(UUID owner) {
		int x, z;

		//TODO designate the location of the island to be created

		buildIsland(owner);

		return new Island(owner);
	}

	public void resetIsland(UUID owner) {
		clearIsland(owner);
		buildIsland(owner);
	}

	private void buildIsland(UUID owner) {
		//TODO Build an "island" in the center of the owners island using a schematic
	}

	private void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}

	public void updateIsland(Island island) {
		data.put(island.getOwner(), island);
	}

	public Island getIsland(UUID owner) {
		if (!data.containsKey(owner)) updateIsland(createIsland(owner));
		return data.get(owner);
	}

	public boolean hasIsland(UUID owner) {
		return data.containsKey(owner);
	}
}