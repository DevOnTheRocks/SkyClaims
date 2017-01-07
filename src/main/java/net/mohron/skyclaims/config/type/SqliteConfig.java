package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SqliteConfig {
	@Setting("Name")
	public String databaseName;
	@Setting("Location")
	public String location;

	public SqliteConfig() {
		location = "./";
		databaseName = "skyclaims";
	}
}