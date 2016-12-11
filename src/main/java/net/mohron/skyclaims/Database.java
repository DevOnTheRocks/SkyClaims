package net.mohron.skyclaims;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Cossacksman on 11/12/2016.
 */
public class Database {
	private String databaseName;

	public Database(String databaseName) throws ClassNotFoundException, SQLException {
		this.databaseName = databaseName;

		// Load the SQLite JDBC driver
		Class.forName("org.sqlite.JDBC");

		// If the database does not exist, create it
		File databaseFile = new File(databaseName);
		if (!databaseFile.exists()) {
			// Get a database connection
			Connection connection = getConnection(databaseName);

			// Create a statement and set the timeout time to 30 seconds
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// Create the database schema
			String table = "create table islands (" +
					"uuid		string," +
					"username	string," +
				")";

			// Create the islands table (execute statement)
			statement.executeUpdate(table);
		}
	}

	/***
	 * Returns a Connection to the database, by name
	 * @param database The name of the database
	 * @return A Connection object to the database
	 * @throws SQLException Thrown if connection issues are encountered
	 */
	public Connection getConnection(String database) throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s", database));
	}

	public DataStore loadData() {
		// TODO: Create a DataStore constructor
		return new DataStore();
	}

	public void saveData(DataStore data) {
		// TODO: Store the data in the database
	}
}
