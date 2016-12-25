package net.mohron.skyclaims;

import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.island.IslandTasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStore {
	protected Map<UUID, Island> data = new HashMap<>();

	public DataStore() {
		//NO-OP
	}

	public DataStore(Map<UUID, Island> data) {
		this.data = data;
	}

	public boolean hasIsland(UUID owner) {
		return data.containsKey(owner);
	}

	public void saveIsland(Island island) {
		data.put(island.getOwner(), island);
	}

	public Island getIsland(UUID owner) {
		if (!hasIsland(owner)) saveIsland(IslandTasks.createIsland(owner));
		return data.get(owner);
	}
}