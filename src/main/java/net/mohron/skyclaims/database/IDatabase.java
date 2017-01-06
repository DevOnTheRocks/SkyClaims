package net.mohron.skyclaims.database;

import net.mohron.skyclaims.world.Island;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Cossacksman on 06/01/2017.
 */
public interface IDatabase {
	Map<UUID, ArrayList<Island>> loadData();
	void saveData(Map<UUID, ArrayList<Island>> islands);
	void saveIsland(Island island);
	void removeIsland(Island island);
}
