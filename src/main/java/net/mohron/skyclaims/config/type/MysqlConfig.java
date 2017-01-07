package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MysqlConfig {
	@Setting("Name")
	public String databaseName;
	@Setting("Location")
	public String location;
	@Setting
	public String tableName;
	@Setting
	public String username;
	@Setting
	public String password;
	@Setting
	public Integer port;

	public MysqlConfig() {
		location = "localhost";
		databaseName = "skyclaims";
		tableName = "islands";
		username = "skyclaims";
		password = "skyclaims";
		port = 3306;
	}
}