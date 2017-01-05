package net.mohron.skyclaims;

import com.flowpowered.math.vector.Vector3i;
import net.mohron.skyclaims.config.type.DatabaseConfig;
import net.mohron.skyclaims.island.Island;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {
	private DatabaseConfig config;
	private String databaseName;
	private String databaseLocation;
	private String islandTableName;

	public Database(String databaseName) {
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
					"ownerUUID	STRING PRIMARY KEY," +
					"claimUUID	STRING," +
					"worldUUID	STRING" +
					"regionX	STRING," +
					"regionZ    STRING," +
					"spawnX			INT," +
					"spawnY			INT," +
					"spawnZ			INT" +
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
	public Map<UUID, Island> loadData() {
		Map<UUID, Island> islands = new HashMap<>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery(String.format("SELECT * FROM %s", config.tableName));

			while (results.next()) {
				UUID ownerId = UUID.fromString(results.getString("ownerUUID"));
				UUID claimId = results.getString("claimUUID") == null ? null : UUID.fromString(results.getString("claimUUID"));
				UUID worldId = UUID.fromString(results.getString("worldUUID"));
				int regionX = results.getInt("regionX");
				int regionZ = results.getInt("regionZ");
				int spawnX = results.getInt("spawnX");
				int spawnY = results.getInt("spawnY");
				int spawnZ = results.getInt("spawnZ");

				Vector3i spawnLocation = new Vector3i(spawnX, spawnY, spawnZ);

				Region region = new Region(regionX, regionZ);

				Island island = new Island(ownerId, claimId, worldId, region, spawnLocation);
				islands.put(ownerId, island);
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
	public void saveData(Map<UUID, Island> islands) {
		for (Island island : islands.values()) {
			try {
				saveIsland(island);
			} catch (Exception e) {
				SkyClaims.getInstance().getLogger().error("Could not save island " + island.getOwnerName() + " " + island.getOwner());
			}
		}
	}

	/**
	 * Saves an individual island to the database
	 *
	 * @param island the island to save
	 */
	public void saveIsland(Island island) {
		String sql = String.format("INSERT OR REPLACE INTO %s(ownerUUID, claimUUID, worldUUID, regionX, regionZ, spawnX, spawnY, spawnZ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getOwner().toString());
			statement.setString(2, island.getClaimId() == null ? null : island.getClaimId().toString());
			statement.setString(3, island.getWorld().getUniqueId().toString());
			statement.setInt(4, island.getRegion().getX());
			statement.setInt(5, island.getRegion().getZ());
			statement.setInt(6, island.getSpawn().getBlockX());
			statement.setInt(7, island.getSpawn().getBlockY());
			statement.setInt(8, island.getSpawn().getBlockZ());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error inserting Island into the database: %s", e.getMessage()));
		}
	}

	/**
	 * remove an individual island from the database
	 *
	 * @param island the island to delete
	 */
	public void removeIsland(Island island) {
		String sql = String.format("DELETE FROM %s WHERE ownerUUID = '?'", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getOwner().toString());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error removing Island from the database: %s", e.getMessage()));
		}
	}
}
