package net.mohron.skyclaims.database;

import net.mohron.skyclaims.world.Island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Cossacksman on 06/01/2017.
 */
public interface IDatabase {
	Map<UUID, HashMap<UUID, Island>> loadData();
	void saveData(Map<UUID, HashMap<UUID, Island>> islands);
	void saveIsland(Island island);
	void removeIsland(Island island);
}
