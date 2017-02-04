package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.File;

@ConfigSerializable
public class SqliteConfig {
	@Deprecated
	@Setting(value = "Name", comment = "Deprecated. Do not use!")
	private String databaseName;
	@Deprecated
	@Setting(value = "Location", comment = "Deprecated. Do not use!")
	private String location;

	public SqliteConfig() {
		databaseName = "skyclaims";
		location = "." + File.separator;
	}

	public StorageConfig getParent() {
		return SkyClaims.getInstance().getConfig().getStorageConfig();
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getLocation() {
		return location;
	}
}