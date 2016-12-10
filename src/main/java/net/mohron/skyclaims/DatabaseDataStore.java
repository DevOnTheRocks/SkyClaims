package net.mohron.skyclaims;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseDataStore extends DataStore {
	private Connection dbConnection = null;
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;

	private SqlService sql;

	DatabaseDataStore (String url, String username, String password) {
		this.dbUrl = url;
		this.dbUsername = username;
		this.dbPassword = password;

		this.init();
	}

	private void init(){
		//TODO Connect to database
		dbConnection = getDataSource("jdbc:" + "")
	}

	public javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}
		return sql.getDataSource(jdbcUrl);
	}

}
