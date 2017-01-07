package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DatabaseConfig {
	@Setting(value = "SqliteDatabase-Type", comment = "The type of data base to use. Supports [SQLite]")
	public String type;
	@Setting
	public String location;
	@Setting
	public String databaseName;
	@Setting
	public String tableName;
	@Setting
	public String username;
	@Setting
	public String password;
	@Setting
	public Integer port;

	public DatabaseConfig() {
		type = "SQLite";
		location = "./";
		databaseName = "skyclaims";
		tableName = "islands";
	}
}
