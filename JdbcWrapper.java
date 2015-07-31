import java.sql.*;

/**
 * Wrapper class to handle JDBC interaction.
 * The DBMS driver is registered in this class' static initializer.
 * 
 * @author Austin Cathey
 */
public class JdbcWrapper
{
	// Our database driver.
	public static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	
	// The name of the database.
	public static final String DATABASE_NAME = "moviedb";
	
	// The database URL.
	public static final String DATABASE_URL = "jdbc:mysql://localhost/" + DATABASE_NAME;
	
	/**
	 * Sets a primitive item or a string in a prepared statement without worrying about exceptions.
	 * 
	 * @param pstmt The PreparedStatement object
	 * @param parameterIndex
	 * @param x
	 * @return True if adding the string succeeded, false otherwise.
	 */
	public static boolean setBasicItem(PreparedStatement pstmt, int parameterIndex, Object x)
	{
		try
		{
			if (x == null)
			{
				throw new NullPointerException("Cannot pass a null object.");
			}
			else if (x instanceof Character)
			{
				return setBasicItem(pstmt, parameterIndex, String.valueOf(x));
			}
			else if (x instanceof Integer)
			{
				pstmt.setInt(parameterIndex, (int)x);
			}
			else if (x instanceof Long)
			{
				pstmt.setLong(parameterIndex, (long)x);
			}
			else if (x instanceof Float)
			{
				pstmt.setFloat(parameterIndex, (float)x);
			}
			else if (x instanceof Double)
			{
				pstmt.setDouble(parameterIndex, (double)x);
			}
			else if (x instanceof String)
			{
				pstmt.setString(parameterIndex, String.valueOf(x));
			}
			return true;
		}
		catch (NullPointerException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
		}
		return false;
	}
	
	/**
	 * Gets the username and password from the user.
	 * 
	 * @param args Command line arguments.
	 * @return A two element string array consisting of {username, password}.
	 */
	public static String[] getUsernameAndPassword(String... args)
	{
		String username, password;
		int length = (args == null) ? 0 : args.length;
		
		// Ask for the username if one wasn't passed.
		if (length < 1)
		{
			username = Input.getString("Enter username (default: root): ", "root");
		}
		else
		{
			username = args[0];
		}
		
		// Ask for a password if one wasn't supplied.
		if (length < 2)
		{
			password = Input.getPassword("Enter password (characters are masked): ");
		}
		else
		{
			password = args[1];
		}
		
		return new String[] { username, password };
	}
	
	/**
	 * Establishes the connection between our application and the DBMS.
	 */
	public static Connection getConnection(String username, String password)
	{
		Connection con = null;		// Connection reference
		
		// Attempt to make the connection.
		try
		{
			con = DriverManager.getConnection(JdbcWrapper.DATABASE_URL, username, password);
		}
		
		// We got here if there was any error.
		// Display the error message and leave.
		catch (SQLException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		
		// Return the reference to our Connection object.
		return con;
	}
	
	/**
	 * Closes a DBMS connection.
	 * 
	 * @param connection The connection to close.
	 */
	public static void closeConnection(Connection connection)
	{
		// Attempt to close the connection, only if a valid reference was passed.
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		
		// Mention there was a problem closing the connection if there was.
		catch (SQLException ex)
		{
			System.out.println("Error closing database connection.");
		}
	}
	
	public static void closeStatement(Statement statement)
	{
		// Attempt to close the statement, only if a valid reference was passed.
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (SQLException ex)
		{
			System.out.println("Error closing Statement object.");
		}
	}
	
	// Static initializer to register database driver.
	static
	{
		try
		{
			Class.forName(JdbcWrapper.DATABASE_DRIVER);
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println("Could not register the MySQL database driver.");
			System.out.println("Maybe it wasn't part of the CLASSPATH?");
			System.exit(1);
		}
	}	
}
