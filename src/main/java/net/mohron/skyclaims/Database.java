package net.mohron.skyclaims;

import java.sql.*;
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

		// If the database does not exist, create it
//		File databaseFile = new File(databaseName);
//		if (!databaseFile.exists()) {
			try {
				// Get a database connection
				Connection connection = getConnection();

				// Create a statement and set the timeout time to 30 seconds
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);

				// Create the database schema
				String table = "CREATE TABLE IF NOT EXISTS islands (" +
						"owner		string," +
						"id			int," +
						"x			int," +
						"y			int," +
						"z			int," +
						"world		int" +
					")";

				// Create the islands table (execute statement)
				statement.executeUpdate(table);

				statement.executeUpdate("INSERT INTO islands (owner, id, x, y, z, world) values ('"+UUID.randomUUID()+"', 25, 255, 137, 482, 1)");
			} catch(SQLException e) {
				e.printStackTrace();
				SkyClaims.getInstance().getLogger().error("Unable to create SkyClaims database");
			}
//		}
	}

	/**
	 * Returns a Connection to the database, by name
	 * @return A Connection object to the database
	 * @throws SQLException Thrown if connection issues are encountered
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseName));
	}

	public DataStore loadData() {
		Map<UUID, Island> dataStore = new HashMap<>();

		// TODO: Create a data constructor
		try {
			Statement statement = getConnection().createStatement();

			ResultSet results = statement.executeQuery("SELECT * FROM islands");

			while (results.next()) {
				int id = results.getInt("id");
				SkyClaims.getInstance().getLogger().info("DATABASE ID: " + id); // Debug log
				Island island =  new Island(UUID.fromString(""+id));
				dataStore.put(UUID.fromString(""+id), island);
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
	 * @param data The DataStore to pull the data from
	 */
	public void saveData(DataStore data) {
		// TODO: Store the data in the database

//		for (Island island : DataStore.data.values()) {
//			island.getOwner();
//			island.getClaim().getID();
//			island.getSpawn().getBlockX();
//			island.getSpawn().getBlockY();
//			island.getSpawn().getBlockZ();
//			island.getSpawn().getExtent();
//		}
	}
}
