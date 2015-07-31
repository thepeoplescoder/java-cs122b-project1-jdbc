/*
 * DBHandler.java
 * 
 * Class to handle accessing the database.
 * 
 * by Austin Cathey and Ankit Verma
 */

// Imports
import java.util.*;
import java.sql.*;

public class DBHandler implements MenuHandler
{
	// The connection associated with this handler.
	private Connection dbConnection;
	
	/**
	 * Constructor for database handler.
	 * 
	 * @param connection The DBMS connection object for this handler.
	 */
	public DBHandler(Connection connection)
	{
		if (connection == null)
		{
			throw new IllegalArgumentException("DBMS connection reference cannot be null.");
		}
		dbConnection = connection;
	}
	
	/**
	 * Gets the DBMS connection associated with this handler.
	 * 
	 * @return The DBMS connection associated with this handler.
	 */
	public Connection getConnection()
	{
		if (dbConnection == null)
		{
			throw new IllegalStateException("This handler has been invalidated, and can no longer be used.");
		}
		return dbConnection;
	}
	
	/**
	 * Gets the movies featuring a given star.
	 */
	private void getMoviesFeaturingStar()
	{
		String fname, lname;
		
		// Show instructions
		System.out.println("You will be asked for a first name and a last name to search.");
		System.out.println("If you choose not to search by a portion of their name, leave that field blank.");
		System.out.println("Leaving both fields blank cancels the search.\n");
		
		// Get the first and last name.
		fname = Input.getString("Enter first name: ");
		lname = Input.getString("Enter last name:  ");
		System.out.println();
		
		// Get the IDs associated with this name.
		ArrayList<Integer> ids = this.getStarIDs(fname, lname);
		
		// Leave if the search was canceled, or if there was an error.
		if (ids == null)
		{
			System.out.println("Search canceled.");
			return;
		}
		
		// Leave if there are no records found.
		else if (ids.isEmpty())
		{
			System.out.println("No records with that name were found!");
			return;
		}
		
		MovieStar star = this.getSingleMovieStar(ids);
		ArrayList<Movie> movies = star.getMovies();
		
		String s = "Movies featuring " + star.getNameFirstLast();
		System.out.println(s);
		for (int index = 0; index < s.length(); index++)
		{
			System.out.print('-');
		}
		System.out.println();
		System.out.println();
		for (Movie movie : movies)
		{
			System.out.println(movie.getTitleAndYear());
		}
	}
	
	private MovieStar getSingleMovieStar(ArrayList<Integer> ids)
	{
		if (ids == null || ids.size() < 1)
		{
			return null;
		}
		
		else if (ids.size() < 2)
		{
			return new MovieStar(ids.get(0));
		}
		
		System.out.println("Multiple search results found:\n");
		for (int id : ids)
		{
			System.out.println(new MovieStar(id).toShortString());
		}
		System.out.println();
		
		String id = Input.getTextOption(false, "Enter the appropriate numeric ID: ", null, ids.toArray());
		System.out.println();
		
		return new MovieStar(Integer.parseInt(id));
	}
	
	/**
	 * Gets a list of IDs associated with a star's name.
	 * 
	 * @param fname First name of star.
	 * @param lname Last name of star.
	 * 
	 * @return An ArrayList of Integers consisting of all the IDs associated with it.
	 */
	private ArrayList<Integer> getStarIDs(String fname, String lname)
	{
		PreparedStatement statement = null;
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		// Return null if there is no first or last name.
		if (fname.length() < 1 && lname.length() < 1)
		{
			return null;
		}
		
		// Look for the ID.
		try
		{
			String query;
			
			// Both first and last name
			if (fname.length() > 0 && lname.length() > 0)
			{
				query = "SELECT id FROM stars WHERE first_name = ? AND last_name = ?";
				statement = getConnection().prepareStatement(query);				
				statement.setString(1, fname);
				statement.setString(2, lname);
			}
			
			// First name only
			else if (fname.length() > 0)
			{
				query = "SELECT id FROM stars WHERE first_name = ?";
				statement = getConnection().prepareStatement(query);
				statement.setString(1, fname);
			}
			
			// Last name only
			else
			{
				query = "SELECT id FROM stars WHERE last_name = ?";
				statement = getConnection().prepareStatement(query);
				statement.setString(1, lname);
			}
			
			// Execute the query.
			ResultSet rs = statement.executeQuery();
			
			// Get the IDs and return them.
			while (rs.next())
			{
				ids.add(rs.getInt("id"));
			}
			return ids;
		}
		
		// Bitch and complain if anything went wrong. :D
		catch (SQLException ex)
		{
			System.out.println(ex.getMessage());
			return null;
		}
		
		// Ensure that we close our Statement object.
		finally
		{
			JdbcWrapper.closeStatement(statement);
		}
	}
	
	private void insertNewStar()
	{
		MovieStar star = insertMovieStarFromConsole();
		
		if (star != null)
		{
			System.out.println(star.getNameFirstLast() + " added successfully:");
			System.out.println(star);
		}
	}
	
	private void insertNewCustomer()
	{
		Customer cust = insertCustomerFromConsole();
		
		if (cust != null)
		{
			System.out.println("Customer added successfully.");
		}
	}
	
	private void deleteCustomer()
	{
		System.out.println("Not yet implemented.");
	}
	
	private void showMetaData()
	{
		System.out.println("Not yet implemented.");
	}
	
	private void enterValidSQLStatement()
	{
		System.out.println("Not yet implemented.");
	}
	
	private class Movie
	{
		private int id;
		private String title;
		private int year;
		private String director;
		private String bannerURL;
		private String trailerURL;
		
		public int getId()
		{
			return id;
		}
		
		public String getTitle()
		{
			return title;
		}
		
		public int getYear()
		{
			return year;
		}
		
		public String getTitleAndYear()
		{
			return String.format("%d -- %s", getYear(), getTitle());
		}
		
		public String getDirector()
		{
			return director;
		}
		
		public String getBannerURL()
		{
			return bannerURL;
		}
		
		public String getTrailerURL()
		{
			return trailerURL;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(
					String.format(
							"ID:          %d\n" +
							"Title:       %s\n" +
							"Year:        %d\n" +
							"Director:    %s\n" +
							"Banner URL:  %s\n" +
							"Trailer URL: %s\n",
							
							getId(),
							getTitle(),
							getYear(),
							getDirector(),
							getBannerURL(),
							getTrailerURL()
					)
			);
			
			return sb.toString();
		}
		
		public Movie(int movieID)
		{
			PreparedStatement pstmt = null;
			
			String query = "SELECT id, title, year, director, banner_url, trailer_url ";
			query       += "FROM movies WHERE id = ?";
			
			try
			{
				pstmt = getConnection().prepareStatement(query);
				pstmt.setInt(1, movieID);
				
				ResultSet rs = pstmt.executeQuery();
				
				if (!rs.next())
				{
					throw new IllegalArgumentException("Invalid ID passed.");
				}
				
				this.id = rs.getInt("id");
				this.title = rs.getString("title");
				this.year = rs.getInt("year");
				this.director = rs.getString("director");
				this.bannerURL = rs.getString("banner_url");
				this.trailerURL = rs.getString("trailer_url");
			}
			catch (SQLException ex)
			{
				System.out.println(ex);
				System.out.println();
				ex.printStackTrace();
				System.exit(1);
			}
			finally
			{
				JdbcWrapper.closeStatement(pstmt);
			}
		}
	}
	
	private MovieStar insertMovieStarFromConsole()
	{
		MovieStar star = getMovieStarFromConsole();		
		return star == null ? null : star.insertIntoStarsDatabase();
	}
	
	private MovieStar getMovieStarFromConsole()
	{
		System.out.println("Enter the information for the star you wish to add.");
		System.out.println("Leave the first and last name blank if you wish to cancel.");
		System.out.println();
		
		String firstName = Input.getString("Enter first name: ");
		String lastName  = Input.getString("Enter last name:  ");
		
		if (firstName.length() < 1 && lastName.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		
		java.sql.Date date = null;
		boolean done = false;
		while (!done)
		{
			try
			{
				String dateString = Input.getString("Enter DOB (yyyy-mm-dd, leave blank to skip): ");
				if (dateString.length() > 0)
				{
					date = java.sql.Date.valueOf(dateString);
					done = true;
				}
				else
				{
					done = true;
				}
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println("Invalid format.");
			}
		}
		
		String photoURL  = Input.getString("Enter photo URL (optional): ");
		System.out.println();
		
		return new MovieStar(firstName, lastName, date, photoURL);
	}
	
	private class MovieStar
	{
		private int id;
		private String firstName;
		private String lastName;
		private java.sql.Date dob;
		private String photoURL;
		
		public MovieStar(String firstName, String lastName, java.sql.Date dob, String photoURL)
		{
			this.id = -1;
			this.firstName = StringUtilities.truncate(firstName, 50);
			this.lastName = StringUtilities.truncate(lastName, 50);
			this.dob = dob;
			this.photoURL = StringUtilities.truncate(photoURL, 200);
			
			// If there is no last name, make the first name the last name.
			if (this.lastName.length() < 1)
			{
				this.lastName = this.firstName;
				this.firstName = "";
			}
		}
		
		public MovieStar insertIntoStarsDatabase()
		{
			// If the star already has a valid ID, then s/he's already in the database.
			if (id >= 0)
			{
				return null;
			}
			
			String insert;
			PreparedStatement pstmt = null;
			
			try
			{
				insert  = "INSERT INTO stars (id, first_name, last_name, dob, photo_url) ";
				insert += "VALUES (NULL, ?, ?, ?, ?)";
				
				pstmt = getConnection().prepareStatement(insert, new String[] {"id"});
				pstmt.setString(1, getFirstName());
				pstmt.setString(2, getLastName());
				pstmt.setDate(3, getDOB());
				pstmt.setString(4, getPhotoURL());
				
				int numRows = pstmt.executeUpdate();
				if (numRows < 1)
				{
					return null;
				}
				else if (numRows > 1)
				{
					throw new SQLException("Too many rows were updated.  This should not happen.");
				}
				
				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys != null && generatedKeys.next())
				{
					this.id = generatedKeys.getInt(1);
				}
			}
			
			catch (SQLException ex)
			{
				System.out.println(ex);
				System.exit(1);
			}
			
			finally
			{
				JdbcWrapper.closeStatement(pstmt);
			}
			
			return this;
		}
		
		public MovieStar(int id)
		{
			PreparedStatement pstmt = null;
			String query = "SELECT id, first_name, last_name, dob, photo_url FROM stars WHERE id = ?";
			
			try
			{
				pstmt = getConnection().prepareStatement(query);
				pstmt.setInt(1, id);
				
				ResultSet rs = pstmt.executeQuery();
				
				if (!rs.next())
				{
					throw new IllegalArgumentException("Invalid ID passed.");
				}
				
				this.id = rs.getInt("id");
				this.firstName = rs.getString("first_name");
				this.lastName = rs.getString("last_name");
				this.dob = rs.getDate("dob");
				this.photoURL = rs.getString("photo_url");
			}
			catch (SQLException ex)
			{
				System.out.println(ex);
				System.out.println();
				ex.printStackTrace();
				System.exit(1);
			}
			finally
			{
				JdbcWrapper.closeStatement(pstmt);
			}
		}
		
		public int getId()
		{
			return id;
		}
		
		public String getFirstName()
		{
			return firstName;
		}
		
		public String getLastName()
		{
			return lastName;
		}
		
		public java.sql.Date getDOB()
		{
			return dob;
		}
		
		public String getPhotoURL()
		{
			return photoURL;
		}
		
		public String getNameFirstLast()
		{
			if (getFirstName().length() < 1)
			{
				return getLastName();
			}
			else if (getLastName().length() < 1)
			{
				return getFirstName();
			}
			return String.format("%s %s", getFirstName(), getLastName());
		}
		
		public String getNameLastFirst()
		{
			if (getFirstName().length() < 1)
			{
				return getLastName();
			}
			else if (getLastName().length() < 1)
			{
				return getFirstName();
			}
			return String.format("%s, %s", getLastName(), getFirstName());
		}
		
		public String toShortString()
		{
			return String.format("%10d -> %s", getId(), getNameLastFirst());
		}
		
		public ArrayList<Movie> getMovies()
		{
			PreparedStatement statementStarsInMovies = null;
			String queryStarsInMovies;
			
			ArrayList<Movie> movies = new ArrayList<Movie>();
			
			if (getId() < 0)
			{
				return null;
			}
			
			try
			{
				queryStarsInMovies  = "SELECT movie_id FROM stars_in_movies ";
				queryStarsInMovies += "WHERE star_id = ?";
				statementStarsInMovies = getConnection().prepareStatement(queryStarsInMovies);
				statementStarsInMovies.setInt(1, getId());
				
				ResultSet rsMovies = statementStarsInMovies.executeQuery();
				
				while (rsMovies.next())
				{
					movies.add(new Movie(rsMovies.getInt("movie_id")));
				}
			}
			
			catch (SQLException ex)
			{
				movies = null;
			}
			
			finally
			{
				JdbcWrapper.closeStatement(statementStarsInMovies);
			}
			
			return movies;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("ID:         " + getId() + "\n");
			sb.append("First Name: " + getFirstName() + "\n");
			sb.append("Last Name:  " + getLastName() + "\n");
			sb.append("DOB:        " + getDOB() + "\n");
			sb.append("Photo URL:  " + getPhotoURL() + "\n");
			
			return sb.toString();
		}
	}
	
	/**
	 * Closes the associated DBMS connection and invalidates this object.
	 */
	public void kill()
	{
		JdbcWrapper.closeConnection(dbConnection);
		dbConnection = null;
	}
	
	/**
	 * This method is just in case the object is garbage collected.
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
		kill();
	}
	
	/**
	 * Required by the MenuHandler interface.
	 */
	public boolean executeOption(int choice)
	{
		// Get all movies featuring a particular star.
		if (choice == Project1.MENU_GET_MOVIES_FEATURING_STAR)
		{
			getMoviesFeaturingStar();
		}
		
		// Insert a new star into the database.
		else if (choice == Project1.MENU_INSERT_NEW_STAR)
		{
			insertNewStar();
		}
		
		// Insert a new customer into the database.
		else if (choice == Project1.MENU_INSERT_NEW_CUSTOMER)
		{
			insertNewCustomer();
		}
		
		// Delete a customer from the database.
		else if (choice == Project1.MENU_DELETE_CUSTOMER)
		{
			deleteCustomer();
		}
		
		// Show the table's metadata.
		else if (choice == Project1.MENU_SHOW_METADATA)
		{
			showMetaData();
		}
		
		// Enter a valid SQL statement.
		else if (choice == Project1.MENU_ENTER_VALID_SQL)
		{
			enterValidSQLStatement();
		}
		
		// Otherwise, this is a terminal option.
		else
		{
			return false;
		}
		
		// Send a signal to keep going.
		System.out.println();
		return true;
	}

	private Customer insertCustomerFromConsole()
	{
		Customer customer = getCustomerFromConsole();		
		return customer == null ? null : customer.insertIntoCustomersDatabase();
	}
	
	private Customer getCustomerFromConsole()
	{
		String firstName, lastName, address, email, password, creditCardId;
		
		System.out.println("Enter the information for the customer wish to add.");
		System.out.println("Leave any field blank to cancel.");
		System.out.println();
		
		firstName = Input.getString("Enter first name: ");
		if (firstName.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		
		lastName  = Input.getString("Enter last name:  ");
		if (lastName.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		
		address = Input.getString("Enter address: ");
		if (address.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		email = Input.getString("Enter email address: ");
		if (email.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		
		password = Input.getString("Enter password: ");
		if (password.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		password = StringUtilities.truncate(password, 20);
		System.out.println("Password is: " + password);
		
		creditCardId = Input.getString("Enter credit card number: ");
		if (creditCardId.length() < 1)
		{
			System.out.println("Insertion canceled.");
			return null;
		}
		
		return new Customer(firstName, lastName, address, email, password, creditCardId);
	}
	
	private class Customer
	{
		private int id;
		private String firstName;
		private String lastName;
		private String address;
		private String email;
		private String password;
		private String creditCardId;
		
		public Customer(String firstName, String lastName, String address, String email, String password, String creditCardId)
		{
			if (StringUtilities.nullOrEmptyExists(firstName, lastName, address, email, password, creditCardId))
			{
				throw new IllegalArgumentException("All fields are required.");
			}
			
			this.firstName = StringUtilities.truncate(firstName, 50);
			this.lastName = StringUtilities.truncate(lastName, 50);
			this.address = StringUtilities.truncate(address, 200);
			this.email = StringUtilities.truncate(email, 50);
			this.password = StringUtilities.truncate(password, 20);
			this.creditCardId = StringUtilities.truncate(creditCardId, 20);
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format(
					"ID:           %d\n" +
					"First Name:   %s\n" +
					"Last Name:    %s\n" +
					"Address:      %s\n" +
					"Email:        %s\n" +
					"Credit Card#: %s\n",
					getId(),
					getFirstName(),
					getLastName(),
					getAddress(),
					getEmail(),
					getPassword()
			));

			return sb.toString();
		}

		public String getCreditCardId()
		{
			return creditCardId;
		}

		public int getId()
		{
			return id;
		}

		public String getFirstName()
		{
			return firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public String getAddress()
		{
			return address;
		}

		public String getEmail()
		{
			return email;
		}

		public String getPassword()
		{
			return password;
		}

		public Customer(int customerID)
		{
			PreparedStatement pstmt = null;
			String query;
			query  = "SELECT id, first_name, last_name, cc_id, address, email, password ";
			query += "FROM customers WHERE id = ?";
			
			try
			{
				pstmt = getConnection().prepareStatement(query);
				pstmt.setInt(1, customerID);
				ResultSet rs = pstmt.executeQuery();
				
				if (!rs.next())
				{
					throw new IllegalArgumentException("Invalid ID passed.");
				}
				
				this.id = rs.getInt("id");
				this.firstName = rs.getString("first_name");
				this.lastName = rs.getString("last_name");
				this.creditCardId = rs.getString("cc_id");
				this.address = rs.getString("address");
				this.email = rs.getString("email");
				this.password = rs.getString("password");
			}
			catch (SQLException ex)
			{
				System.out.println(ex);
				System.out.println();
				ex.printStackTrace();
				System.exit(1);
			}
			finally
			{
				JdbcWrapper.closeStatement(pstmt);
			}
		}
		
		public Customer insertIntoCustomersDatabase()
		{
			// If the star already has a valid ID, then s/he's already in the database.
			if (getId() >= 0)
			{
				return null;
			}
			
			String insert;
			PreparedStatement pstmt = null;
			
			try
			{
				insert  = "INSERT INTO customers ";
				insert += "(id, first_name, last_name, cc_id, address, email, password) ";
				insert += "VALUES ";
				insert += "(null, ?, ?, ?, ?, ?, ?)";
				
				pstmt = getConnection().prepareStatement(insert, new String[] {"id"});
				pstmt.setString(1, getFirstName());
				pstmt.setString(2, getLastName());
				pstmt.setString(3, getCreditCardId());
				pstmt.setString(4, getAddress());
				pstmt.setString(5, getEmail());
				pstmt.setString(6, getPassword());
				
				int numRows = pstmt.executeUpdate();
				if (numRows < 1)
				{
					System.out.println("Could not add to database.");
					System.out.println("Possibly you entered an invalid credit card number?");
					return null;
				}
				else if (numRows > 1)
				{
					throw new SQLException("Too many rows were updated.  This should not happen.");
				}
				
				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys != null && generatedKeys.next())
				{
					this.id = generatedKeys.getInt(1);
				}
			}
			
			catch (SQLException ex)
			{
				System.out.println(ex);
				System.exit(1);
			}
			
			finally
			{
				JdbcWrapper.closeStatement(pstmt);
			}
			
			return this;
		}
	}

	private void _deleteCustomer()
	{
		PreparedStatement pstmt = null;
		String query = "DELETE id, first_name, last_name, address, email, password ";
		query += "FROM customers WHERE id = ?";
		try
		{
			pstmt = getConnection().prepareStatement(query);
			// pstmt.setInt(1, customerID);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next())
			{
				throw new IllegalArgumentException("Invalid ID passed.");
			}
		}
		catch (SQLException ex)
		{
			System.out.println(ex);
			System.out.println();
			ex.printStackTrace();
			System.exit(1);
		}
		finally
		{
			JdbcWrapper.closeStatement(pstmt);
		}
	}
}