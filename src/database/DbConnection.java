package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

	protected Connection conn;
	protected Statement stmt;
    
    public DbConnection(){
    	
		try {
			//String connectionUrl = "jdbc:sqlserver://172.17.200.1:1433;"
		   	//      + "databaseName=MicroGrid;user=Longo;password=Admin.Longo";
			String connectionUrl = "jdbc:sqlserver://DESKTOP-5KCT80R\\CITADEL;"
					+"databaseName=MicroGrid;user=sa;password=ciaociao";
			conn = DriverManager.getConnection(connectionUrl);
			stmt = conn.createStatement();
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
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
        try{
            conn.close();
        }catch(SQLException e){
			e.printStackTrace();
        }
    }
}
