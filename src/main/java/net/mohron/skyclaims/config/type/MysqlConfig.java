package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MysqlConfig {
	@Setting("Name")
	private String databaseName = "skyclaims";
	@Setting("Location")
	private String location = "localhost";
	@Setting("Table-Prefix")
	private String tablePrefix = "";
	@Setting("Username")
	private String username = "skyclaims";
	@Setting("Password")
	private String password = "skyclaims";
	@Setting("Port")
	private int port = 3306;

	public int getPort() {
		return port;
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
}