package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class StorageConfig {
	@Setting(value = "Type", comment = "The type of data storage to use. Supports [SQLite, MySQL]")
	public String type;
	@Setting(value = "SQLite")
	public SqliteConfig sqlite;
	@Setting(value = "MySQL")
	public MysqlConfig mysql;

	public StorageConfig() {
		type = "SQLite";
		sqlite = new SqliteConfig();
		mysql = new MysqlConfig();
	}
}