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

package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MysqlConfig {
	@Setting("Name")
	private String databaseName;
	@Setting("Location")
	private String location;
	@Setting("Table-Prefix")
	private String tablePrefix;
	@Setting("Username")
	private String username;
	@Setting("Password")
	private String password;
	@Setting("Port")
	private Integer port;

	public MysqlConfig() {
		databaseName = "skyclaims";
		location = "localhost";
		tablePrefix = "";
		username = "skyclaims";
		password = "skyclaims";
		port = 3306;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getLocation() {
		return location;
	}

	public String getPassword() {
		return password;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public String getUsername() {
		return username;
	}

	public int getPort() {
		return (port != null) ? port : 3306;
	}
}