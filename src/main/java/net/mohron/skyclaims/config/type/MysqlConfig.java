package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MysqlConfig {
	@Setting("Name")
	public String databaseName;
	@Setting("Location")
	public String location;
	@Setting("Table-Name")
	public String tableName;
	@Setting("Username")
	public String username;
	@Setting("Password")
	public String password;
	@Setting("Port")
	public Integer port;

	public MysqlConfig() {
		tableName = "islands";
		location = "localhost";
		databaseName = "skyclaims";
		username = "skyclaims";
		password = "skyclaims";
		port = 3306;
	}
}