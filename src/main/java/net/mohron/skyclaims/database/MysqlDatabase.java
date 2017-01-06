package net.mohron.skyclaims.database;

import net.mohron.skyclaims.config.type.DatabaseConfig;
import net.mohron.skyclaims.world.Island;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Cossacksman on 06/01/2017.
 */
public class MysqlDatabase implements IDatabase {
	private DatabaseConfig config;
	private String databaseLocation;
	private String islandTableName;
	private String username;
	private String password;
	private int port;

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(databaseLocation, username, password);
	}

	public Map<UUID, ArrayList<Island>> loadData() {
		return null;
	}

	public void saveData(Map<UUID, ArrayList<Island>> islands) {

	}

	public void saveIsland(Island island) {

	}

	public void removeIsland(Island island) {

	}
}
