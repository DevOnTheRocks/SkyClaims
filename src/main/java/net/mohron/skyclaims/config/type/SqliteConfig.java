package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SqliteConfig {
	@Setting("Name")
	private String databaseName = "skyclaims";
	@Setting("Location")
	private String location = "/";

	public String getDatabaseName() {
		return databaseName;
	}

	public String getLocation() {
		return location;
	}
}