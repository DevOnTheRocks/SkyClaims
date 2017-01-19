package net.mohron.skyclaims.database;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.MysqlConfig;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.Island;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MysqlDatabase implements IDatabase {
	private MysqlConfig config;
	private String connectionString;
	private String databaseLocation;
	private String databaseTableName;
	private String username;
	private String password;
	private Integer port;

	public MysqlDatabase() {
		this.config = ConfigUtil.getMysqlDatabaseConfig();
		databaseLocation = config.location;
		databaseTableName = config.tableName;
		username = config.username;
		password = config.password;
		port = ConfigUtil.getDatabasePort();
		connectionString = String.format("jdbc:mysql://%s:%s/%s", databaseLocation, port, databaseTableName);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to load the JDBC driver");
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to connect to the database, check the console");
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
					"locked			BOOLEAN" +
					")", databaseTableName);

			// Create the islands table (execute statement)
			statement.executeUpdate(table);
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to create SkyClaims database");
		}
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString, username, password);
	}

	public HashMap<UUID, Island> loadData() {
		HashMap<UUID, Island> islands = Maps.newHashMap();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery(String.format("SELECT * FROM %s", config.tableName));

			while (results.next()) {
				UUID islandId = UUID.fromString(results.getString("island"));
				UUID ownerId = UUID.fromString(results.getString("owner"));
				UUID claimId = UUID.fromString(results.getString("claim"));
				int x = results.getInt("spawnX");
				int y = results.getInt("spawnY");
				int z = results.getInt("spawnZ");
				boolean locked = results.getBoolean("locked");

				Vector3i spawnLocation = new Vector3i(x, y, z);
				Island island = new Island(islandId, ownerId, claimId, spawnLocation, locked);

				islands.put(islandId, island);
			}
			return islands;
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		return islands;
	}

	public void saveData(Map<UUID, Island> islands) {
		for (Island island : islands.values())
			saveIsland(island);
	}

	public void saveIsland(Island island) {
		String sql = String.format("REPLACE INTO %s(island, owner, claim, spawnX, spawnY, spawnZ) VALUES(?, ?, ?, ?, ?, ?)", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getUniqueId().toString());
			statement.setString(2, island.getOwnerUniqueId().toString());
			statement.setString(3, island.getClaim().getUniqueId().toString());
			statement.setInt(4, island.getSpawn().getBlockX());
			statement.setInt(5, island.getSpawn().getBlockY());
			statement.setInt(6, island.getSpawn().getBlockZ());
			statement.setBoolean(7, island.isLocked());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error inserting Island into the database: %s", e.getMessage()));
		}
	}

	public void removeIsland(Island island) {
		String sql = String.format("DELETE FROM %s WHERE island = ?", config.tableName);

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getUniqueId().toString());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error removing Island from the database: %s", e.getMessage()));
		}
	}
}
