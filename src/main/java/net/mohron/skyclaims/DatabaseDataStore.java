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
		try {
			dbConnection = getDataSource(String.format("jdbc:%s", dbUrl)).getConnection(dbUsername, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
		if (sql == null)
			if (Sponge.getServiceManager().provide(SqlService.class).isPresent())
				sql = Sponge.getServiceManager().provide(SqlService.class).get();

		return sql.getDataSource(jdbcUrl);
	}

}
