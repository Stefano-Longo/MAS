package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

	//static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
	//static final String DB_URL = "jdbc:sqlserver://DESKTOP-5KCT80R\\SQLEXPRESS;databaseName=MicroGrid";
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/MicroGrid";
	
	//static final String USER = "sa";
	//static final String PASS = "ciaociao";
	static final String USER = "root";
	static final String PASS = "";
	
	public Connection conn = null;
	public Statement stmt = null;
	public ResultSet rs = null;
    
    public DbConnection(){
    	
		try {
			//String connectionUrl = "jdbc:sqlserver://172.17.200.1:1433;"
		   	//      + "databaseName=MicroGrid;user=Longo;password=Admin.Longo";
		    Class.forName(JDBC_DRIVER);
			//conn = DriverManager.getConnection(connectionUrl);
		    conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public Connection getConnection()
    {
		return conn;
    }
    
    public void openTransaction(Connection conn){
    	try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void rollback(Connection conn){
    	try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void commit(Connection conn){
    	try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void connClose(){
	    try { rs.close(); } catch (Exception e) { }
	    try { stmt.close(); } catch (Exception e) { }
	    try { conn.close(); } catch (Exception e) { }
    }
}
