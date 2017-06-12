package image.explorer.model;

import java.sql.*; // JDBC & MySQL

public class DBConnect {
	
	// JDBC Connector version: mysql-connector-java-5.1.40-bin.jar
	private String jdbcDriver;	// JDBC driver name.
	
	// Database credentials.
	private String dbUser;
	private String dbPass;
	private String dbName;
	
	private int dbClientPort;	// Port on client machine which talks to server's database.
	private String dbURL;		// Database URL.

	private Connection conn = null;	// Database connection.
	private Statement stmt = null;	// Database query statement.
	
	protected DBConnect() {
		initialize();
	}

	// Initialize variables and connect to the server's MySQL database.
	private void initialize() {
		try {
			
			this.jdbcDriver = "com.mysql.jdbc.Driver";
			Class.forName(this.jdbcDriver);
			
			this.dbUser = "root";
			this.dbPass = "$xP7*-Md@8H";
			this.dbName = "dbImageExplorer";
			
			this.dbClientPort = 3307;
			this.dbURL = "jdbc:mysql://localhost:" + this.dbClientPort + "/" + this.dbName + "?useSSL=false";
			
			// Connect to remote database
			this.conn = DriverManager.getConnection(this.dbURL, this.dbUser, this.dbPass);
			
		} catch (ClassNotFoundException e) {
			System.out.println("\nERROR: Driver class not found.");
		} catch (SQLException e) {
			System.out.println("\nERROR: Can't connect to database.");
		}
	}
	
	protected void closeConnections() {
		try {
			
			System.out.println("\nClosing database connections...");
	
			if(this.stmt != null)
				this.stmt.close();
			
			if(this.conn != null && !this.conn.isClosed())
				this.conn.close();
	
			System.out.println("...Database connections closed");
		
		} catch (SQLException e) {
			System.out.println("\nERROR: Closing database connections failed.");
		}
	}
	
	// [INSERT], [UPDATE], and [DELETE] queries.
	protected boolean sqlExecute(String query) {
		boolean ret = false;

		try {
			stmt = conn.createStatement();
			System.out.println("\nExecuting statement....");
			stmt.execute(query); // Execute command
			System.out.println("...Statement exection SUCCESS");
			System.out.println("\nStatement executed:\n" + query);
			ret = true;
			// Clean-up
			stmt.close();
		} catch (SQLException e) { // Handle errors for JDBC
			System.out.println("...Statement execution FAILURE");
			System.out.println("\nStatement failed:\n" + query);
		}

		return ret; // Return boolean (execution success = true)
	}

	// [SELECT] queries.
	protected String[][] sqlSelect(int count, String query) {
		String[][] ret = null;

		try {
			stmt = conn.createStatement();
			System.out.println("\nExecuting statement....");
			ResultSet rs = stmt.executeQuery(query); // Execute command.

			System.out.println("...Statement exection SUCCESS");
			System.out.println("\nStatement executed:\n" + query);

			ret = new String[count][rs.getMetaData().getColumnCount()];

			int i = 0;
			while(rs.next()){
				for(int j = 0; j < rs.getMetaData().getColumnCount(); j++) {
					ret[i][j] = rs.getString(j + 1);
				}
				i++;
			}

			// Clean-up.
			rs.close();
			stmt.close();
		} catch (SQLException e) { // Handle errors for JDBC.
			System.out.println("...Statement execution FAILURE");
		}

		return ret; // Return 2D-array of results.
	}

	// [COUNT] queries.
	// Also runs each time a [SELECT] query is called,
	// in order to count how many results will be returned.
	protected int sqlCount(String query) {
		int count = -1;

		try {
			stmt = conn.createStatement();
			System.out.println("\nExecuting statement....");
			ResultSet rs = stmt.executeQuery(query); // Execute command.

			System.out.println("...Statement exection SUCCESS");
			System.out.println("\nStatement executed:\n" + query);

			while(rs.next()){
				count  = rs.getInt("Count(*)");
				System.out.println("\nResult:\nCount(*) = " + count);
			}

			// Clean-up.
			rs.close();
			stmt.close();
		} catch (SQLException e) { // Handle errors for JDBC.
			System.out.println("...Statement execution FAILURE");
		}

		return count; // Return int result of counting (-1 = failure).
	}
	
	protected int sqlExecuteResult(String query) {
		int ret = -1;

		try {
			stmt = conn.createStatement();
			System.out.println("\nExecuting statement....");
			ResultSet rs = stmt.executeQuery(query); // Execute command.

			System.out.println("...Statement exection SUCCESS");
			System.out.println("\nStatement executed:\n" + query);

			rs.absolute(1);
			ret = rs.getInt(1);
			System.out.println("RS[0] = " + ret);

			// Clean-up.
			rs.close();
			stmt.close();
		} catch (SQLException e) { // Handle errors for JDBC.
		    System.out.println("...Statement execution FAILURE");
		}

		return ret; // Return int of next FileID.
	}
	
}
