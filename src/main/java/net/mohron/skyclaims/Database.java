package net.mohron.skyclaims;

import net.mohron.skyclaims.island.Island;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {
	private String databaseName;

	public Database(String databaseName) {
		this.databaseName = databaseName;

		// Load the SQLite JDBC driver
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to load the JDBC driver");
		}

		try {
			// Get a database connection
			Connection connection = getConnection();

			// Create a statement and set the timeout time to 30 seconds
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// Create the database schema
			String table = "CREATE TABLE IF NOT EXISTS islands (" +
					"owner		STRING PRIMARY KEY," +
					"id			STRING," +
					"x			INT," +
					"y			INT," +
					"z			INT," +
					"world		STRING" +
					")";

			// Create the islands table (execute statement)
			statement.executeUpdate(table);

			statement.executeUpdate("INSERT INTO islands (owner, id, x, y, z, world) values ('" + UUID.randomUUID() + "', '" + UUID.randomUUID() + "', 255, 137, 482, '" + UUID.randomUUID() + "')");
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
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseName));
	}

	public DataStore loadData() {
		Map<UUID, Island> dataStore = new HashMap<>();

		try {
			Statement statement = getConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM islands");

			while (results.next()) {
				String owner = results.getString("owner");
				Island island = new Island(UUID.fromString(owner));
				dataStore.put(UUID.fromString(owner), island);
				SkyClaims.getInstance().getLogger().info("Owner UUID: " + owner); // Debug log
				SkyClaims.getInstance().getLogger().info("SIZE: " + dataStore.values().size());
			}

			return new DataStore(dataStore);
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		return null;
	}

	/**
	 * Inserts/Updates the database with the data-storage in memory
	 *
	 * @param dataStore The DataStore to pull the data from
	 */
	public void saveData(DataStore dataStore) throws SQLException {
		for (Island island : dataStore.data.values()) {
			String sql = "INSERT OR UPDATE INTO islands(owner, id, x, y, z, world) VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = getConnection().prepareStatement(sql);
			statement.setString(1, island.getOwner().toString());
			statement.setString(2, island.getClaim().getID().toString());
			statement.setInt(3, island.getSpawn().getBlockX());
			statement.setInt(4, island.getSpawn().getBlockY());
			statement.setInt(5, island.getSpawn().getBlockZ());
			statement.setString(6, island.getClaim().world.getUniqueId().toString());

			SkyClaims.getInstance().getLogger().info("UPDATING DB");

			if (statement.execute())
				SkyClaims.getInstance().getLogger().info("UPDATE WORKED!");
		}
	}
}