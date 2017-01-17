package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MysqlConfig {
	@Setting("DatabaseName")
	public String databaseName;
	@Setting("Location")
	public String location;
	@Setting("Username")
	public String username;
	@Setting("Password")
	public String password;
	@Setting("Port")
	public Integer port;

	public MysqlConfig() {
		location = "localhost";
		port = 3306;
		databaseName = "skyclaims";
		username = "skyclaims";
		password = "skyclaims";
	}
}