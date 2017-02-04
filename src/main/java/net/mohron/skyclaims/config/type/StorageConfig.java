package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.File;

@ConfigSerializable
public class StorageConfig {
	@Setting(value = "Location", comment = "The location to store SkyClaims data. Default: *CONFIG*/data")
	private String location;
	@Setting(value = "Type", comment = "The type of data storage to use. Supports [SQLite, MySQL]")
	private String type;
	@Setting(value = "SQLite")
	private SqliteConfig sqliteConfig;
	@Setting(value = "MySQL")
	private MysqlConfig mysqlConfig;

	public StorageConfig() {
		location = String.format("*CONFIG*%sdata", File.separator);
		type = "SQLite";
		sqliteConfig = new SqliteConfig();
		mysqlConfig = new MysqlConfig();
	}

	public String getLocation() {
		return location.replace("*CONFIG*", SkyClaims.getInstance().getConfigDir().toString());
	}

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