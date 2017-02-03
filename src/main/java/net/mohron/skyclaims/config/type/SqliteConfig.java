package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SqliteConfig {
	@Setting("Name")
	private String databaseName = "skyclaims";

	public String getDatabaseName() {
		return databaseName;
	}

}