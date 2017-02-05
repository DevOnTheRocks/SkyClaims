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