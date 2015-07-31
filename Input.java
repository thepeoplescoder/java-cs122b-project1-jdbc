/*
 * Input.java
 * 
 * Source file containing methods for user input.
 * 
 * by Austin Cathey (catheya@uci.edu)
 */

// Imports
import java.io.*;
import java.util.*;

/**
 * A class containing static methods to facilitate getting user input.
 * 
 * @author Austin Cathey (catheya@uci.edu)
 */
public class Input
{
	// Grab the console.
	private static Console console = System.console();
	
	// Grab a scanner just in case.
	public final Scanner scanner = new Scanner(System.in);
	
	// Create a bogus NumberFormatException.
	private static final NumberFormatException dummyNumberFormatException = new NumberFormatException();
	
	/**
	 * Gets a string from the console.
	 * 
	 * @param prompt The prompt to be displayed to the user.
	 * @param theDefault The default value if nothing was entered.  If this is
	 *                   null, then there is no default value.
	 * @return The string that was entered.  This will never be null.
	 */
	public static String getString(String prompt, String theDefault)
	{
		// Display the prompt to the user.
		System.out.print(prompt);
		
		// Get the text entered by the user.
		String text = console.readLine();
		
		// If there is a default string, and the user typed nothing,
		if ( (text == null || text.length() < 1) && theDefault != null )
		{
			// then return that default string.
			return theDefault;
		}
		
		// Otherwise, return the string that was typed.
		return text != null ? text : "";
	}
	
	/**
	 * Convenience method to get a string from the console.
	 * Equivalent to calling getString(String, String) with no default string.
	 * 
	 * @param prompt The prompt to be displayed to the user.
	 * @return The string that was entered.  This will never be null.
	 */
	public static String getString(String prompt)
	{
		// Get the string, no default value.
		return Input.getString(prompt, null);
	}
	
	/**
	 * Gets a password from the user.
	 * 
	 * @param prompt The prompt to be displayed.
	 * @return The password that was entered.
	 */
	public static String getPassword(String prompt)
	{
		// Convert the user's password to a string.
		return new String(
				
				// This displays the prompt and gets the password.
				console.readPassword("%s", prompt)
		);
	}
	
	/**
	 * Gets an option from the user based on a series of strings.
	 * 
	 * @param caseSensitive True if the choices are case sensitive, false otherwise.
	 * @param prompt The prompt to be displayed to the user.
	 * @param theDefault The default choice.  Note that this means nothing if it's not one of our options.
	 * @param options The options that the user gets to pick from.
	 * 
	 * @return The option that was selected.
	 */
	public static String getTextOption(boolean caseSensitive, String prompt, String theDefault, Object... options)
	{
		boolean done;
		String userOption = null;
		
		// Only perform the action of this method if there are valid options.
		if (options != null)
		{
			// Complain if a null was passed in our options.
			for (Object x : options)
			{
				if (x == null)
				{
					throw new NullPointerException("getTextOption(): null options are not allowed.");
				}
			}
			
			// Prompt the user if there are more options.
			if (options.length > 1)
			{
				
				// We aren't done until we say we are.
				done = false;
				
				// Get the option from the user.
				while (!done)
				{
					// Ask the user for their option.
					userOption = Input.getString(prompt, theDefault);
					
					// Look through our options.  If we find a match, then we are done.
					for (int index = 0; !done && index < options.length; index++)
					{
						String s = String.valueOf(options[index]);
						done = caseSensitive ?					// Choose comparison style.
								userOption.equals(s) :			// Case sensitive
								userOption.equalsIgnoreCase(s);	// Case insensitive
					}
					
					// Notify the user that their choice was invalid if we aren't done,
					// since that's the only reason why we'd even still be in here.
					if (!done)
					{
						System.out.println("Invalid option selected.\n");
					}
				}
			}
			
			// If there is only one option to choose from, then there is no need to prompt them.
			else if (options.length > 0)
			{
				return String.valueOf(options[0]);
			}
		}
		
		// Return the user's option.
		return userOption;
	}
	
	/**
	 * Gets a menu option from the user.
	 * 
	 * @param mh The MenuHandler that will take action based on a selected option.
	 * @param prompt The prompt to be displayed to the user.
	 * @param theDefault The default value.  If there is no default value, set this to a nonpositive value.
	 * @param optionStrings The menu options.
	 * 
	 * @return The absolute value of the return value will equal the option that the
	 *         user has selected.  If the return value is negative, it is supposed to mean
	 *         that a "terminal" option was selected.  Zero is returned on error.
	 */
	public static int getMenuOption(MenuHandler mh, String prompt, int theDefault, String... optionStrings)
	{
		boolean done;
		String defaultString;
		int choice = theDefault;
		
		// Return zero if there are no menu options to choose from.
		if (optionStrings == null || optionStrings.length < 1)
		{
			return 0;
		}
		
		// Prepare a default string.
		// Note that if the default value is outside of the range of
		// possible options, then the default option is NOT used.
		if (theDefault < 1 || theDefault > optionStrings.length)
		{
			defaultString = null;
		}
		else
		{
			defaultString = String.valueOf(theDefault);
		}
		
		// The menu loop.
		done = false;
		while (!done)
		{
			// Display the menu.
			for (int index = 0; index < optionStrings.length; index++)
			{
				System.out.println("" + (index + 1) + ") " + optionStrings[index]);
			}
			System.out.println();
			
			// Prompt the user for input.
			String option = Input.getString(prompt, defaultString);
			
			// Process the option.
			try
			{
				// We are done if the choice is in our range.
				choice = Integer.parseInt(option);
				if ((choice >= 1) && (choice <= optionStrings.length))
				{
					done = true;
				}
				
				// This is just to force the error message to be shown.
				else
				{
					throw Input.dummyNumberFormatException;
				}
			}
			
			// We got here if an invalid option was selected.
			catch (NumberFormatException ex)
			{
				System.out.println("Invalid option selected.");
				System.out.println("Please choose an option between 1 and " + optionStrings.length);
			}
			
			// Skip a line to keep things looking neat.
			System.out.println();
		}
		
		// Execute the choice if a MenuHandler was passed.
		if (mh != null)
		{
			if (!mh.executeOption(choice))
			{
				choice = -choice;
			}
		}
		
		// Return the selected choice.
		return choice;
	}
}
