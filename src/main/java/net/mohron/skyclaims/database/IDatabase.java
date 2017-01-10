package net.mohron.skyclaims.database;

import net.mohron.skyclaims.world.Island;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IDatabase {
	HashMap<UUID, Island> loadData();

	void saveData(Map<UUID, Island> islands);

	void saveIsland(Island island);

	void removeIsland(Island island);
}
