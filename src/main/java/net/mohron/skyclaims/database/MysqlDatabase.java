package net.mohron.skyclaims.database;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.MysqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDatabase extends Database {
	private MysqlConfig config;
	private String connectionString;
	private String databaseLocation;
	private String databaseTablePrefix;
	private String username;
	private String password;
	private Integer port;

	public MysqlDatabase() {
		this.config = SkyClaims.getInstance().getConfig().getStorageConfig().getMysqlConfig();
		databaseLocation = config.getLocation();
		databaseTablePrefix = config.getTablePrefix();
		username = config.getUsername();
		password = config.getPassword();
		port = config.getPort();
		connectionString = String.format("jdbc:mysqlConfig://%s:%s/%s", databaseLocation, port, "islands");

		try {
			Class.forName("com.mysqlConfig.jdbc.Driver");
			getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to load the JDBC driver");
		} catch (SQLException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Unable to connect to the database, check the console");
		}

		createTable();
	}

	Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString, username, password);
	}
}
