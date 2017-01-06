package net.mohron.skyclaims.database;

import com.flowpowered.math.vector.Vector3i;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.DatabaseConfig;
import net.mohron.skyclaims.world.Island;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SqliteDatabase implements IDatabase {
	private DatabaseConfig config;
	private String databaseName;
	private String databaseLocation;
	private String islandTableName;

	public SqliteDatabase(String databaseName) {
		this.config = SkyClaims.getInstance().getConfig().database;
		this.databaseName = databaseName;
		this.databaseLocation = config.location;
		this.islandTableName = config.tableName;

		// Load the SQLite JDBC driver
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to load the JDBC driver");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);

			// Create the database schema
			String table = String.format("CREATE TABLE IF NOT EXISTS %s (" +
					"island			STRING PRIMARY KEY" +
					"owner			STRING," +
					"claim			STRING," +
					"spawnX			INT," +
					"spawnY			INT," +
					"spawnZ			INT," +
					"world			STRING" +
					"locked			BOOLEAN" +
				")", islandTableName);

			// Create the islands table (execute statement)
			statement.executeUpdate(table);
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to create SkyClaims database");
		}
	}

	/**
	 * Returns a Connection to the database, by name
	 *
	 * @return A Connection object to the database
	 * @throws SQLException Thrown if connection issues are encountered
	 */
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db", databaseLocation, File.separator, config.databaseName));
	}

	/**
	 * Creates Island objects and stores them in a DataStore to be loaded into memory
	 *
	 * @return Returns a new DataStore generated from the database data
	 */
	public Map<UUID, ArrayList<Island>> loadData() {
		Map<UUID, ArrayList<Island>> islands = new HashMap<>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery(String.format("SELECT * FROM %s", config.tableName));

			while (results.next()) {
				ArrayList<Island> islandList = new ArrayList<>();

				UUID islandId = UUID.fromString(results.getString("island"));
				UUID ownerId = UUID.fromString(results.getString("owner"));
				UUID claimId = UUID.fromString(results.getString("claim"));
				UUID worldId = UUID.fromString(results.getString("world"));
				int x = results.getInt("spawnX");
				int y = results.getInt("spawnY");
				int z = results.getInt("spawnZ");

				Vector3i spawnLocation = new Vector3i(x, y, z);

				Island island = new Island(islandId, ownerId, worldId, claimId, spawnLocation);
				islandList.add(island);

				if (islands.containsKey(ownerId))
					islands.get(ownerId).add(island);
				else
					islands.put(ownerId, islandList);
			}
			return islands;
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		return islands;
	}

	/**
	 * Inserts/Updates the database with the data-storage in memory
	 *
	 * @param islands The map in memory to pull the data from
	 */
	public void saveData(Map<UUID, ArrayList<Island>> islands) {
		for (ArrayList<Island> islandList : islands.values())
			for (Island island : islandList)
				saveIsland(island);
	}

	/**
	 * Saves an individual island to the database
	 *
	 * @param island the island to save
	 */
	public void saveIsland(Island island) {
		String sql = String.format("INSERT OR REPLACE INTO %s(island, owner, claim, spawnX, spawnY, spawnZ, world) VALUES(?, ?, ?, ?, ?, ?)", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getUniqueId().toString());
			statement.setString(2, island.getOwner().toString());
			statement.setString(3, island.getClaimId().toString());
			statement.setInt(4, island.getSpawn().getBlockX());
			statement.setInt(5, island.getSpawn().getBlockY());
			statement.setInt(6, island.getSpawn().getBlockZ());
			statement.setString(7, island.getWorld().getUniqueId().toString());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error inserting Island into the database: %s", e.getMessage()));
		}
	}

	/**
	 * remove an individual island to the database
	 *
	 * @param island the island to remove
	 */
	public void removeIsland(Island island) {
		String sql = String.format("DELETE FROM %s WHERE island = '?'", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getOwner().toString());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error removing Island from the database: %s", e.getMessage()));
		}
	}
}