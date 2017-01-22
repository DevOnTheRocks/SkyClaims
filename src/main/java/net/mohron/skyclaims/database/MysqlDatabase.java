package net.mohron.skyclaims.database;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.MysqlConfig;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.Island;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MysqlDatabase extends Database {
	private MysqlConfig config;
	private String connectionString;
	private String databaseLocation;
	private String databaseTableName;
	private String username;
	private String password;
	private Integer port;

	public MysqlDatabase() {
		this.config = ConfigUtil.getMysqlDatabaseConfig();
		databaseLocation = config.location;
		databaseTableName = config.tableName;
		username = config.username;
		password = config.password;
		port = ConfigUtil.getDatabasePort();
		connectionString = String.format("jdbc:mysql://%s:%s/%s", databaseLocation, port, databaseTableName);

		try {
			Class.forName("com.mysql.jdbc.Driver");
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
