package net.mohron.skyclaims;

import com.flowpowered.math.vector.Vector3i;
import net.mohron.skyclaims.island.Island;
import org.spongepowered.api.world.World;

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

	/**
	 * Creates Island objects and stores them in a DataStore to be loaded into memory
	 *
	 * @return Returns a new DataStore generated from the database data
	 */
	public Map<UUID, Island> loadData() {
		Map<UUID, Island> islands = new HashMap<>();

		try {
			Statement statement = getConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM islands");

			while (results.next()) {
				UUID claimId = UUID.fromString(results.getString("id"));
				UUID ownerId = UUID.fromString(results.getString("owner"));
				UUID worldId = UUID.fromString(results.getString("world"));
				Vector3i claimLowerBound = new Vector3i(results.getInt("x"), results.getInt("y"), 0);
				// TODO:- Some funky math to get the size (or store it)
				Vector3i claimUpperBound = new Vector3i(results.getInt("x"), results.getInt("y"), 0);

				Island island = new Island(ownerId, worldId, claimId);

				islands.put(ownerId, island);
				SkyClaims.getInstance().getLogger().debug("Owner UUID: " + ownerId);
				SkyClaims.getInstance().getLogger().debug("SIZE: " + islands.values().size());
			}

			return islands;
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		return null;
	}

	/**
	 * Inserts/Updates the database with the data-storage in memory
	 *
	 * @param islands The map in memory to pull the data from
	 */
	public void saveData(Map<UUID, Island> islands) {
		for (Island island : islands.values()) {
			String sql = "INSERT OR UPDATE INTO islands(owner, id, x, y, z, world) VALUES(?, ?, ?, ?, ?, ?)";

			try {
				PreparedStatement statement = getConnection().prepareStatement(sql);
				statement.setString(1, island.getOwner().toString());
				statement.setString(2, island.getClaimId().toString());
				statement.setInt(3, island.getSpawn().getBlockX());
				statement.setInt(4, island.getSpawn().getBlockY());
				statement.setInt(5, island.getSpawn().getBlockZ());
				statement.setString(6, island.getClaim().world.getUniqueId().toString());

				SkyClaims.getInstance().getLogger().debug("UPDATING DB");

				if (statement.execute())
					SkyClaims.getInstance().getLogger().debug("UPDATE WORKED!");
			} catch (SQLException e) {
				SkyClaims.getInstance().getLogger().error(String.format("Error updating the database: %s", e.getMessage()));
			}
		}
	}

	/**
	 * Saves an individual island to the database
	 * @param island the island to save
	 */
	public void saveIsland(Island island) {
		String sql = "INSERT OR UPDATE INTO islands(owner, id, x, y, z, world) VALUES(?, ?, ?, ?, ?, ?)";

		try {
			PreparedStatement statement = getConnection().prepareStatement(sql);
			statement.setString(1, island.getOwner().toString());
			statement.setString(2, island.getClaimId().toString());
			statement.setInt(3, island.getSpawn().getBlockX());
			statement.setInt(4, island.getSpawn().getBlockY());
			statement.setInt(5, island.getSpawn().getBlockZ());
			statement.setString(6, island.getClaim().world.getUniqueId().toString());

			SkyClaims.getInstance().getLogger().debug("UPDATING DB");

			if (statement.execute())
				SkyClaims.getInstance().getLogger().debug("UPDATE WORKED!");
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error inserting Island into the database: %s", e.getMessage()));
		}
	}
}
