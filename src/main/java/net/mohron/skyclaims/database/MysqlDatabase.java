package net.mohron.skyclaims.database;

import com.flowpowered.math.vector.Vector3i;
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
	private String hostname;
	private String databaseName;
	private String username;
	private String password;
	private Integer port;

	public MysqlDatabase() {
		this.config = ConfigUtil.getMysqlDatabaseConfig();
		hostname = ConfigUtil.getDatabaseHostname();
		databaseName = ConfigUtil.getDatabaseName();
		username = ConfigUtil.getDatabaseUsername();
		password = ConfigUtil.getDatabasePassword();
		port = ConfigUtil.getDatabasePort();
		connectionString = String.format("jdbc:mysql://%s:%s/%s", hostname, port, databaseName);

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
			//Create Table with appropriate Schema
			statement.executeUpdate(String.format(Schemas.IslandTable,"islands"));
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to create SkyClaims database");
		}
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString, username, password);
	}

	public HashMap<UUID, Island> loadData() {
		HashMap<UUID, Island> islands = new HashMap<>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery(String.format("SELECT * FROM islands"));

			while (results.next()) {
				UUID islandId = UUID.fromString(results.getString("UUID"));
				UUID ownerId = UUID.fromString(results.getString("Player"));
				UUID claimId = UUID.fromString(results.getString("Claim"));
				int x = results.getInt("spawnX");
				int y = results.getInt("spawnY");
				int z = results.getInt("spawnZ");
				//boolean locked = results.getBoolean("locked");

				Vector3i spawnLocation = new Vector3i(x, y, z);
				Island island = new Island(islandId, ownerId, claimId, spawnLocation, false);

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
		String sql = "REPLACE INTO islands(UUID, Player, Claim, RegionX, RegionY, spawnX, spawnY, spawnZ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getUniqueId().toString());
			statement.setString(2, island.getOwner().get().getUniqueId().toString());
			statement.setString(3, island.getClaim().getUniqueId().toString());
			statement.setInt(4, island.getRegion().getX());
			statement.setInt(5, island.getRegion().getZ());
			statement.setInt(6, island.getSpawn().getBlockX());
			statement.setInt(7, island.getSpawn().getBlockY());
			statement.setInt(8, island.getSpawn().getBlockZ());
			//statement.setFloat(9, island.getSpawn().getYaw());
			//statement.setFloat(10, island.getSpawn().getPitch());
			//statement.setBoolean(7, island.isLocked());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error inserting Island into the database: %s", e.getMessage()));
		}
	}

	public void removeIsland(Island island) {
		String sql = "DELETE FROM islands WHERE UUID = ?";

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, island.getUniqueId().toString());

			statement.execute();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error(String.format("Error removing Island from the database: %s", e.getMessage()));
		}
	}
}
