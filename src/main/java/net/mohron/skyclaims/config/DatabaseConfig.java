package net.mohron.skyclaims.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DatabaseConfig {
	@Setting
	public String type;
	@Setting
	public String location;
	@Setting
	public String tableName;
	@Setting
	public String username;
	@Setting
	public String password;

	public DatabaseConfig() {

	}
}
