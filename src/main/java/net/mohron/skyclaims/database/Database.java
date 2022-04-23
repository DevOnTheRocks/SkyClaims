/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;

public abstract class Database implements IDatabase {

  abstract Connection getConnection() throws SQLException;

  /***
   * Creates a table using SQL syntax for Sqlite or Mysql
   */
  void createTable() {
    try (Statement statement = getConnection().createStatement()) {
      statement.setQueryTimeout(30);

      // Create the database schema
      String table = "CREATE TABLE IF NOT EXISTS islands (" +
          "island			STRING PRIMARY KEY," +
          "owner			STRING," +
          "claim			STRING," +
          "spawnX			INT," +
          "spawnY			INT," +
          "spawnZ			INT," +
          "locked			BOOLEAN" +
          ")";

      // Create the islands table (execute statement)
      statement.executeUpdate(table);
    } catch (SQLException e) {
      SkyClaims.getInstance().getLogger().error("Unable to create SkyClaims database", e);
    }
  }

  /**
   * Creates Island objects and stores them in a DataStore to be loaded into memory
   *
   * @return Returns a new DataStore generated from the database data
   */
  public Map<UUID, Island> loadData() {
    HashMap<UUID, Island> islands = Maps.newHashMap();

    try (Statement statement = getConnection().createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM islands")) {

      while (results.next()) {
        UUID islandId = UUID.fromString(results.getString("island"));
        UUID ownerId = UUID.fromString(results.getString("owner"));
        UUID claimId = UUID.fromString(results.getString("claim"));
        int x = results.getInt("spawnX");
        int y = results.getInt("spawnY");
        int z = results.getInt("spawnZ");
        boolean locked = results.getBoolean("locked");

        Vector3d spawnLocation = new Vector3d(x, y, z);
        Island island = new Island(islandId, ownerId, claimId, spawnLocation, locked);

        islands.put(islandId, island);
      }
      return islands;
    } catch (SQLException e) {
      SkyClaims.getInstance().getLogger().error("Unable to read from the database.", e);
    }

    SkyClaims.getInstance().getLogger().info("Loaded SkyClaims MySQL Data. Count: {}", islands.size());
    return islands;
  }

  /**
   * Inserts/Updates the database with the data-storage in memory
   *
   * @param islands The collection in memory to pull the data from
   */
  public void saveData(Collection<Island> islands) {
    for (Island island : islands) {
      saveIsland(island);
    }
  }

  /**
   * Inserts/Updates the database with the data-storage in memory
   *
   * @param islands The map in memory to pull the data from
   */
  public void saveData(Map<UUID, Island> islands) {
    for (Island island : islands.values()) {
      saveIsland(island);
    }
  }

  /**
   * Saves an individual island to the database
   *
   * @param island the island to save
   */
  public void saveIsland(Island island) {
    String sql = "REPLACE INTO islands(island, owner, claim, spawnX, spawnY, spawnZ, locked) VALUES(?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
      statement.setString(1, island.getUniqueId().toString());
      statement.setString(2, island.getOwnerUniqueId().toString());
      statement.setString(3, island.getClaimUniqueId().toString());
      statement.setInt(4, island.getSpawn().getLocation().getBlockX());
      statement.setInt(5, island.getSpawn().getLocation().getBlockY());
      statement.setInt(6, island.getSpawn().getLocation().getBlockZ());
      statement.setBoolean(7, island.isLocked());

      statement.execute();
    } catch (SQLException e) {
      SkyClaims.getInstance().getLogger().error("Error inserting Island into the database:", e);
    }
  }

  /**
   * removes an individual island to the database
   *
   * @param island the island to remove
   */
  public void removeIsland(Island island) {
    String sql = "DELETE FROM islands WHERE island = ?";

    try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
      statement.setString(1, island.getUniqueId().toString());

      statement.execute();
    } catch (SQLException e) {
      SkyClaims.getInstance().getLogger().error("Error removing Island from the database:", e);
    }
  }

  /**
   * Count the columns of a row in the database
   *
   * @return The column count of the schema
   */
  public int countColumns() {
    int total = 0;

    String sql = "SELECT * FROM islands LIMIT 1";
    try (PreparedStatement statement = getConnection().prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {
      return rs.getMetaData().getColumnCount();
    } catch (SQLException e) {
      SkyClaims.getInstance().getLogger().error("Unable to get database column count:", e);
    }

    return total;
  }
}
