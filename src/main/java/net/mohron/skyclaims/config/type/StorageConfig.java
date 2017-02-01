package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class StorageConfig {
	@Setting(value = "Type", comment = "The type of data storage to use. Supports [SQLite, MySQL]")
	private String type = "SQLite";
	@Setting(value = "SQLite")
	private SqliteConfig sqliteConfig = new SqliteConfig();
	@Setting(value = "MySQL")
	private MysqlConfig mysqlConfig = new MysqlConfig();

	public String getType() {
		return type;
	}

	public MysqlConfig getMysqlConfig() {
		return mysqlConfig;
	}

	public SqliteConfig getSqliteConfig() {
		return sqliteConfig;
	}
}