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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.MysqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDatabase extends Database {

	private String connectionString;
	private String databaseLocation;
	private String username;
	private String password;
	private String name;
	private Integer port;

	public MysqlDatabase() {
		MysqlConfig config = SkyClaims.getInstance().getConfig().getStorageConfig().getMysqlConfig();
		this.databaseLocation = config.getLocation();
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.name = config.getDatabaseName();
		this.port = config.getPort();
		this.connectionString = String.format("jdbc:mysql://%s:%s/%s", databaseLocation, port, name);
		this.tableName = SkyClaims.getInstance().getConfig().getStorageConfig().getMysqlConfig().getTablePrefix() + "island";
		this.primaryKey = "(`island`(36))";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			getConnection();
		} catch (ClassNotFoundException e) {
			SkyClaims.getInstance().getLogger().error("Unable to load MySQL JDBC driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			SkyClaims.getInstance().getLogger().error("Unable to connect to the database!");
			e.printStackTrace();
		}

		createTable();
	}

	Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString, username, password);
	}
}
