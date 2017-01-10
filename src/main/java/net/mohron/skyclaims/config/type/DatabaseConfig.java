package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DatabaseConfig {
	@Setting(value = "Database-Type", comment = "The type of data base to use. Supports [SQLite, MySQL]")
	public String type;
	@Setting(value = "SQLite")
	public SqliteConfig sqlite;
	@Setting(value = "MySQL")
	public MysqlConfig mysql;

	public DatabaseConfig() {
		type = "SQLite";
		sqlite = new SqliteConfig();
		mysql = new MysqlConfig();
	}
}