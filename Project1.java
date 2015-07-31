/*
 * Project1.java
 * 
 * Entry class for Project 1.
 * 
 * by Austin Cathey and Ankit Verma
 */

/**
 * 
 * @author thepe_000
 */
public class Project1
{
	// The menu options.
	private static final String[] menuOptions = {
		"Get movies featuring a given star",
		"Insert a new star into the database",
		"Insert a new customer into the database",
		"Delete a customer from the database",
		"Show internal database information",
		"Enter valid SELECT/UPDATE/INSERT/DELETE SQL command",
		"Switch database user",
		"Exit the program"
	};
	
	// The corresponding integers that map to the menu options.
	public static final int MENU_GET_MOVIES_FEATURING_STAR = 1;
	public static final int MENU_INSERT_NEW_STAR = 2;
	public static final int MENU_INSERT_NEW_CUSTOMER = 3;
	public static final int MENU_DELETE_CUSTOMER = 4;
	public static final int MENU_SHOW_METADATA = 5;
	public static final int MENU_ENTER_VALID_SQL = 6;
	public static final int MENU_SWITCH_DB_USER = 7;
	public static final int MENU_QUIT = Project1.menuOptions.length;
	
	/**
	 * Entry point.
	 * 
	 * @param args The command line arguments, separated by a space.
	 */
	public static void main(String[] args) throws Exception
	{
		int choice = Project1.MENU_SWITCH_DB_USER;
		String[] userPass = JdbcWrapper.getUsernameAndPassword(args);
		DBHandler dbHandler;
		
		// Keep running this loop until the user decides to quit.
		while (!Project1.isQuitting(choice))
		{
			// Connect to the DBMS.
			dbHandler = new DBHandler(JdbcWrapper.getConnection(userPass[0], userPass[1]));
			System.out.print("Connected to DBMS: ");
			System.out.print(dbHandler.getConnection().getMetaData().getDatabaseProductName() + " v");
			System.out.println(dbHandler.getConnection().getMetaData().getDatabaseProductVersion());
			System.out.println();
			
			// This is where the fun starts.
			try
			{
				// Stay in the main menu if that's what we should be doing.
				while ((choice = mainMenu(dbHandler)) >= 0) {}
			}
			
			// Okay, we're out of the main menu, so that means
			// that there's no reason to maintain the current DBMS
			// connection.  Go ahead and close it.
			finally
			{
				// Close the connection and allow this object to be eligible for GC.
				dbHandler.kill();
				dbHandler = null;
				
				// If we need to switch users, then go ahead and do that.
				if (Math.abs(choice) == Project1.MENU_SWITCH_DB_USER)
				{
					userPass = JdbcWrapper.getUsernameAndPassword((String [])null);
				}
			}
		}
	}
	
	/**
	 * The main menu.  All menu actions are deployed in this method.
	 * 
	 * @param connection The DBMS connection.
	 * 
	 * @return A positive value if the menu should keep running, otherwise false.
	 */
	public static int mainMenu(DBHandler handler)
	{
		// Complain if the handler is null.
		if (handler == null)
		{
			throw new IllegalArgumentException("This shouldn't happen.  The DBMS handler shouldn't be null.");
		}
		
		// Get the user's choice.
		return Input.getMenuOption(handler, "Enter your choice: ", 0, Project1.menuOptions);
	}
	
	/**
	 * Checks to see if the user selected the quit option.
	 * 
	 * @param option The option that the user selected.
	 * @return True if the user selected the quit option, false otherwise.
	 */
	private static boolean isQuitting(int option)
	{
		return Math.abs(option) == Project1.MENU_QUIT;
	}
}