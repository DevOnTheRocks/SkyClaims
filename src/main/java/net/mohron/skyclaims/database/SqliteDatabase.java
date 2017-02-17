/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.database;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.StorageConfig;
import net.mohron.skyclaims.world.Island;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class SqliteDatabase extends Database {
	private StorageConfig config;
	private Connection dbConnection;

	public SqliteDatabase() {
		this.config = SkyClaims.getInstance().getConfig().getStorageConfig();

		// Load the SQLite JDBC driver
		try {
			Class.forName("org.sqlite.JDBC");
			dbConnection = DriverManager.getConnection(String.format("jdbc:sqlite:%s%sskyclaims.db", config.getLocation(), File.separator));
			SkyClaims.getInstance().getLogger().info("Successfully connected to SkyClaims SQLite DB.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to load the JDBC driver");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		createTable();
		migrate();
	}

	/**
	 * Returns a Connection to the database, by name
	 *
	 * @return A Connection object to the database
	 * @throws SQLException Thrown if connection issues are encountered
	 */
	Connection getConnection() throws SQLException {
		return dbConnection;
	}

	/**
	 * Migrates the database from an old schema to a new one
	 */
	public void migrate() {
		HashMap<UUID, Island> islands;

		SkyClaims.getInstance().getLogger().info(String.format("Table size: %s", countColumns()));

		if (countColumns() == 6) {
			SkyClaims.getInstance().getLogger().info("Migrating the database..");

			backup();
			islands = loadLegacyData();

			String sql = "DROP TABLE IF EXISTS islands";
			try (PreparedStatement statement = getConnection().prepareStatement(sql)) {

				SkyClaims.getInstance().getLogger().info("Dropping the islands table..");

				statement.executeUpdate();
				SkyClaims.getInstance().getLogger().info("Dropped the islands table.");

				SkyClaims.getInstance().getLogger().info("Re-initializing islands table...");
				createTable();
				SkyClaims.getInstance().getLogger().info("Re-initialized islands table.");

				SkyClaims.getInstance().getLogger().info("Repopulating islands table...");
				saveData(islands);
				SkyClaims.getInstance().getLogger().info("Repopulated islands table, migration complete.");
			} catch (SQLException e) {
				e.printStackTrace();
				SkyClaims.getInstance().getLogger().error("Unable to drop islands table, check the console");
			}
		}

	}

	/**
	 * Creates a file backup of the existing database in the configured directory
	 */
	public void backup() {
		File inputFile = new File(String.format("%s%sskyclaims.db", config.getLocation(), File.separator));
		File outputFile = new File(String.format("%s%sskyclaims_backup.db", config.getLocation(), File.separator));

		try {
			FileUtils.copyFile(inputFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Error occurred while backing up legacy SQLite DB.");
		}
	}

	/**
	 * Creates Island objects and stores them in a DataStore to be loaded into memory
	 *
	 * @return Returns a new DataStore generated from the database data
	 */
	@Override
	public HashMap<UUID, Island> loadData() {
		HashMap<UUID, Island> islands = Maps.newHashMap();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM islands");
			UUID claimId;
			while (results.next()) {
				if (results.getString("claim").length() != 36) {
					claimId = UUID.randomUUID();
				} else {
					claimId = UUID.fromString(results.getString("claim"));
				}
				UUID islandId = UUID.fromString(results.getString("island"));
				UUID ownerId = UUID.fromString(results.getString("owner"));

				int x = results.getInt("spawnX");
				int y = results.getInt("spawnY");
				int z = results.getInt("spawnZ");
				boolean locked = results.getBoolean("locked");

				Vector3d spawnLocation = new Vector3d(x, y, z);
//				SkyClaims.getInstance().getLogger().debug(String.format("Loading %s, %s, %s, %s, %s", islandId, ownerId, claimId, spawnLocation.toString(), locked));
				Island island = new Island(islandId, ownerId, claimId, spawnLocation, locked);

				islands.put(islandId, island);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		SkyClaims.getInstance().getLogger().info("Loaded SkyClaims SQLite Data. Count: " + islands.size());
		return islands;
	}

	/**
	 * Load legacy data from the database from the previous schema
	 *
	 * @return A hashmap of the ported islands
	 */
	private HashMap<UUID, Island> loadLegacyData() {
		HashMap<UUID, Island> islands = Maps.newHashMap();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM islands");

			while (results.next()) {
				UUID ownerId = UUID.fromString(results.getString("owner"));
				UUID claimId = UUID.fromString(results.getString("id"));
				int x = results.getInt("x");
				int y = results.getInt("y");
				int z = results.getInt("z");

				UUID id = UUID.randomUUID();
				Vector3d spawnLocation = new Vector3d(x, y, z);
				Island island = new Island(id, ownerId, claimId, spawnLocation, true);

				islands.put(id, island);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to read from the database.");
		}

		SkyClaims.getInstance().getLogger().info("Loaded SkyClaims SQLite Legacy Data. Count: " + islands.size());
		return islands;
	}
}