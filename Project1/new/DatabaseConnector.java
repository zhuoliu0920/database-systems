
import java.sql.*;
public class DatabaseConnector
{

	private Database db = null;
	private Connection conn = null;
	private Statement st = null;
	
	DatabaseConnector(Database db)
	{
		this.db = db;
	}
	
	public void connect()
	{
		try
		{
			disconnect();
			conn = DriverManager.getConnection(db.getURL(), db.getUserName(), db.getPassWord());
			st = conn.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void disconnect()
	{
		//If there already exists an active connection, close it.
		try
		{
			if(conn != null && conn.isClosed() == false)
			{
				conn.close();
				conn = null;
			}
			if(st != null)
    			if ( st.isClosed() == false)
			{
				st.close();
				st = null;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isConnected()
	{
		return conn != null;
	}
	
	public ResultSet executeQuery(String sqlStatement)
	{
		if(isConnected())
		{
			try
			{
				return st.executeQuery(sqlStatement);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	public void execute(String sqlStatement)
	{
		try
		{
			st.execute(sqlStatement);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public DatabaseMetaData getMetaData()
	{
		try
		{
			return conn.getMetaData();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
