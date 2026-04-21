package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import admin.Role;
import entityClasses.CreateInfo;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import entityClasses.ReportInfo;
import entityClasses.DiscussionBoardContent;

/*******
 * <p>
 * Title: Database Class.
 * </p>
 * 
 * <p>
 * Description: This is an in-memory database built on H2. Detailed
 * documentation of H2 can be found at https://www.h2database.com/html/main.html
 * (Click on "PDF (2MP) for a PDF of 438 pages on the H2 main page.) This class
 * leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00 2025-04-29 Updated and expanded from the version produce by on
 *          a previous version by Pravalika Mukkiri and Ishwarya Hidkimath
 *          Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the
 * connection to the database, and performing operations such as user
 * registration, login validation, handling invitation codes, and numerous other
 * database related functions.
 */
public class Database {
   //testing out github - ahmed
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	// Shared variables used within this class
	private Connection connection = null; // Singleton to access the database
	private Statement statement = null; // The H2 Statement is used to construct queries

	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p>
	 * Method: Database
	 * </p>
	 * 
	 * <p>
	 * Description: The default constructor used to establish this singleton object.
	 * </p>
	 * 
	 */

	public Database() {

	}

	/*******
	 * <p>
	 * Method: connectToDatabase
	 * </p>
	 * 
	 * <p>
	 * Description: Used to establish the in-memory instance of the H2 database from
	 * secondary storage.
	 * </p>
	 *
	 * @throws SQLException when the DriverManager is unable to establish a
	 *                      connection
	 * 
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/*******
	 * <p>
	 * Method: createTables
	 * </p>
	 * 
	 * <p>
	 * Description: Used to create new instances of the two database tables used by
	 * this class.
	 * </p>
	 * 
	 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), " + "lastName VARCHAR (255), " + "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), " + "adminRole BOOL DEFAULT FALSE, " + "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE," + "mustResetPass BOOL DEFAULT FALSE," + "accountActive BOOL DEFAULT TRUE)";
		statement.execute(userTable);

		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), " + "role VARCHAR(10))";
		statement.execute(invitationCodesTable);
		
		//Create new table for all posts
		String postTable =
			    "CREATE TABLE IF NOT EXISTS posts (" +
			    "  id VARCHAR(36) UNIQUE," +
			    "  userName VARCHAR(255)," +
			    "  header VARCHAR(100)," +
			    "  body VARCHAR(10000)," +
			    "  createdAt VARCHAR(100)," +
			    "  edited BOOL DEFAULT FALSE," +
			    "  editedAt VARCHAR(100)," +
			    "  solved BOOL DEFAULT FALSE" +
			    ")";
		statement.execute(postTable);
		
		//Create new table for all replies
		String replyTable =
			    "CREATE TABLE IF NOT EXISTS replies (" +
			    "  id VARCHAR(36) UNIQUE," +
			    "  postId VARCHAR(36)," +
			    "  userName VARCHAR(255)," +
			    "  body VARCHAR(10000)," +
			    "  createdAt VARCHAR(100)," +
			    "  edited BOOL NOT NULL DEFAULT FALSE," +
			    "  editedAt VARCHAR(100)" +
			    ")";
	
		statement.execute(replyTable);
		
		String reportTable = "CREATE TABLE IF NOT EXISTS reports (" +
			    "  id VARCHAR(36) UNIQUE," +
			    "  number INT" +
			    ")";
		
		statement.execute(reportTable);
	}

	/*******
	 * <p>
	 * Method: isDatabaseEmpty
	 * </p>
	 * 
	 * <p>
	 * Description: If the user database has no rows, true is returned, else false.
	 * </p>
	 * 
	 * @return true if the database is empty, else it returns false
	 * 
	 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/*******
	 * <p>
	 * Method: getNumberOfUsers
	 * </p>
	 * 
	 * <p>
	 * Description: Returns an integer .of the number of users currently in the user
	 * database.
	 * </p>
	 * 
	 * @return the number of user records in the database.
	 * 
	 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

	/*******
	 * <p>
	 * Method: register(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a new row in the database using the user parameter.
	 * </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or
	 *                      executing it.
	 * 
	 * @param user specifies a user object to be added to the database.
	 * 
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);

			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);

			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);

			currentMiddleName = user.getMiddleName();
			pstmt.setString(4, currentMiddleName);

			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);

			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);

			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);

			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);

			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);

			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);

			pstmt.executeUpdate();
		}

	}
	
	
	
	
	/*******
	 * <p>
	 * Method: getContentById
	 * </p>
	 * 
	 * <P>
	 * Description: This is a multi-purpose class which will return a post saved post or reply class based on the relevant object stored in
	 * either of the relevant sql tables. If none of the tables contain the unique id it will return null 
	 * </p>
	 * 
	 * @param id the unique id of the content
	 * 
	 * @return DiscussionBoardContent the relevant object of the user content
	 */
	public DiscussionBoardContent getContentById(String id) {
		
		
		//Attempt to find a related post
	    String postSql = "SELECT id, userName, header, body, createdAt, edited, editedAt, solved " +
	                     "FROM posts " +
	                     "WHERE id = ?";

	    //Search posts table for ID
	    try (PreparedStatement ps = connection.prepareStatement(postSql)) {
	        ps.setString(1, id);

	        try (ResultSet rs = ps.executeQuery()) {
	        	
	        	//If Present Construct the post object
	            if (rs.next()) {
	                CreateInfo info = new CreateInfo(
	                        rs.getString("userName"),
	                        rs.getString("createdAt"),
	                        rs.getBoolean("edited"),
	                        rs.getString("editedAt"),
	                        rs.getString("id")
	                );

	                Post post = new Post(
	                        info,
	                        rs.getString("header"),
	                        rs.getString("body"),
	                        rs.getBoolean("solved")
	                );

	                return post; //Post Found Return a Post Object
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    //If there is not post found, look for a reply with a matching Id
	    String replySql = "SELECT id, postId, userName, body, createdAt, edited, editedAt " +
	                      "FROM replies " +
	                      "WHERE id = ?";

	    //Search replies table for matching id
	    try (PreparedStatement ps = connection.prepareStatement(replySql)) {
	        ps.setString(1, id);

	        try (ResultSet rs = ps.executeQuery()) {
	        	
	        	//If Reply is found construct a reply object
	            if (rs.next()) {
	                String parentId = rs.getString("postId");

	                CreateInfo info = new CreateInfo(
	                        rs.getString("userName"),
	                        rs.getString("createdAt"),
	                        rs.getBoolean("edited"),
	                        rs.getString("editedAt"),
	                        rs.getString("id")
	                );

	                Reply reply = new Reply(
	                        info,
	                        rs.getString("body"),
	                        parentId
	                );

	                return reply;  //Reply was found return reply object
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    //Nothing was found return null
	    return null;
	}
	
	/*******
	 * <p>
	 * Method: mergeReport
	 * </p>
	 * 
	 * <P>
	 * This function either creates a new saved value of report content in the reports table, or will
	 * update existing report values based on the unique id of the content
	 * </p>
	 * 
	 * @param report the report object to merge into the database
	 */
	public void mergeReport(ReportInfo report) {
		
		//Merge operation to either save new column or update old ones
	    String sql = "MERGE INTO reports (id, number) " +
	                 "KEY(id) " +
	                 "VALUES (?, ?)";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, report.getID());
	        ps.setInt(2, report.getNumber());
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();  //Print Stack
	    }
	}
	
	
	/*******
	 * <p>
	 * Method: getAllReports
	 * </p>
	 * 
	 * <P>
	 * This function obtains a list of all the report info objects saved into the SQL table.
	 * </p>
	 * 
	 * @return ArrayList<ReportInfo> a list of all saved report info objects
	 */
	public ArrayList<ReportInfo> getAllReports() {
	    ArrayList<ReportInfo> reports = new ArrayList<>(); //Create new array list

	    String sql = "SELECT id, number FROM reports"; //Select all report ids and numbers from the reports table

	    try (PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	    	//While there are rows to save
	        while (rs.next()) {
	        	
	        	//Construct report info object and add to array list
	            String id = rs.getString("id");
	            int number = rs.getInt("number");
	            DiscussionBoardContent content = getContentById(id);
	            ReportInfo report = new ReportInfo(content, number, id);
	            reports.add(report);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace(); // or your logging mechanism
	    }

	    return reports; //Return the list
	}
	
	
	/*******
	 * <p>
	 * Method: deleteReport
	 * </p>
	 * 
	 * <P>
	 * This function removes a report from the database when given the id
	 * </p>
	 * 
	 * @param id the unique id of the user content
	 */
	public void deleteReport(String id) {
	    String sql = "DELETE FROM reports WHERE id = ?"; //Deletion Statement

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, id);
	        ps.executeUpdate(); //Remove from db
	    } catch (SQLException e) {
	        e.printStackTrace(); //Trace Stack on error
	    }
	}
	
	
	/*******
	 * <p>
	 * Method: getPostReplies
	 * </p>
	 * 
	 * <P>
	 * Description: This function creates a hash map of all the saved post replies. The reason it is stored in a hash map is because
	 * every reply also saves the id of it's parent that way replies for posts can easily be obtained by using the parentId as the key.
	 * </p>
	 * 
	 * @return HashMap<String,ArrayList<Reply>> a hash map of all the replies, each postId maps to a list of replies under that post
	 */
	public HashMap<String,ArrayList<Reply>> getPostReplies()
	{
		HashMap<String, ArrayList<Reply>> toReturn = new HashMap<String, ArrayList<Reply>>();

	    String sql = "SELECT id, postId, userName, body, createdAt, edited, editedAt " +
	                 "FROM replies " +
	                 "ORDER BY createdAt DESC";

	    try (PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) 
	        {
	        	String parentId = rs.getString("postId");
	        	CreateInfo tempInfo = new CreateInfo(rs.getString("userName"), rs.getString("createdAt"), rs.getBoolean("edited"), rs.getString("editedAt"), rs.getString("id"));
	        	Reply tempReply = new Reply(tempInfo, rs.getString("body"), parentId);
	        	toReturn.putIfAbsent(parentId, new ArrayList<Reply>());
	        	toReturn.get(parentId).add(tempReply);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // or log properly
	    }
	    return toReturn;
	}
	
	
	/*******
	 * <p>
	 * Method: getDiscussionPosts
	 * </p>
	 * 
	 * <P>
	 * Description: This method creates a list of every discussion post currently saved in the database.
	 * It instantiates Post objects for each saved post then returns them in a list.
	 * </p>
	 * 
	 * @return ArrayList<Post> a list of all the currently saved posts
	 */
	public ArrayList<Post> getDiscussionPosts()
	{
		ArrayList<Post> toReturn = new ArrayList<Post>();

	    String sql = "SELECT id, userName, header, body, createdAt, edited, editedAt, solved " +
	                 "FROM posts " +
	                 "ORDER BY createdAt DESC";

	    try (PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) 
	        {
	        	CreateInfo tempInfo = new CreateInfo(rs.getString("userName"), rs.getString("createdAt"), rs.getBoolean("edited"), rs.getString("editedAt"), rs.getString("id"));
	        	Post tempPost = new Post(tempInfo, rs.getString("header"), rs.getString("body"), rs.getBoolean("solved"));
	        	toReturn.add(tempPost);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // or log properly
	    }
	    return toReturn;
	}
	
	
	/*******
	 * <p>
	 * Method: createPost(Post post)
	 * </p>
	 * 
	 * <P>
	 * Description: When passed a post object it will save it to the posts table in the database
	 * </p>
	 * 
	 * @param Post, the new post to be saved 
	 */
	public void createPost(Post post) {
	    String insertPost = "INSERT INTO posts (id, userName, header, body, createdAt, edited, editedAt) "
	                      + "VALUES (?, ?, ?, ?, ?, ?, ?)"; //Sql statement

	    try (PreparedStatement pstmt = connection.prepareStatement(insertPost)) {
	        // id: use the one on the Post or generate one
	        String id = (post.getID() == null || post.getID().isBlank())
	                ? UUID.randomUUID().toString()
	                : post.getID();
	        
	        pstmt.setString(1, id); //Set the id
	        // userName, header, body
	        pstmt.setString(2, post.getUser());  //Set the user name   
	        pstmt.setString(3, post.getHeader()); //Set the header
	        pstmt.setString(4, post.getBody()); //Set the body

	        pstmt.setString(5, post.getCreateDate()); //Set the date created

	        pstmt.setBoolean(6, post.getEdited()); //Set edited

	        String editedAt = post.getEditedTime();    // null if never edited
	        if (editedAt == null || editedAt.isBlank()) {
	            pstmt.setNull(7, Types.VARCHAR);
	        } else {
	            pstmt.setString(7, editedAt);
	        }

	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	 }
	
	/*******
	 * <p>
	 * Method: void updatePost(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Updates a post and the relevant variables
	 * </p>
	 * 
	 * @param id is the updated post
	 * 
	 */
	public void updatePost(Post post) {
		String query = "UPDATE posts SET header = ?, body = ?, edited = ?, editedAt = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, post.getHeader());
			pstmt.setString(2, post.getBody());
			pstmt.setBoolean(3, post.getEdited());
			pstmt.setString(4, post.getEditedTime());
			pstmt.setString(5, post.getID());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: void updatePostSolvedStatus(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Updates a post's solved status and the relevant variables
	 * </p>
	 * 
	 * @param id is the updated post
	 * 
	 */
	public void updatePostSolvedStatus(Post post) {
		String query = "UPDATE posts SET solved = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, post.getSolved());
			pstmt.setString(2, post.getID());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*******
	 * <p>
	 * Method: void deletePost(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Deletes a post from the database
	 * </p>
	 * 
	 * @param post is the post to delete
	 */
	public void deletePost(Post post) {
		String query = "DELETE FROM posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, post.getID());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: void deletePostReplies(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Deletes all replies associated with a post from the database
	 * </p>
	 * 
	 * @param post is the post which holds the replies to delete
	 */
	public void deletePostReplies(Post post) {
		String query = "DELETE FROM replies WHERE postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, post.getID());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*******
	 * <p>
	 * Method: createReply(Reply reply)
	 * </p>
	 * 
	 * <P>
	 * Description: When passed a reply object it will save it to the replies table in the database
	 * </p>
	 * 
	 * @param Reply, the new reply to be saved 
	 */
	public void createReply(Reply reply) {
	    String insertPost = "INSERT INTO replies (id, postId, userName, body, createdAt, edited, editedAt) "
	                      + "VALUES (?, ?, ?, ?, ?, ?, ?)"; //Sql statement

	    try (PreparedStatement pstmt = connection.prepareStatement(insertPost)) {
	        // id: use the one on the Post or generate one
	        String id = (reply.getID() == null || reply.getID().isBlank())
	                ? UUID.randomUUID().toString()
	                : reply.getID();
	        
	        pstmt.setString(1, id); //Set the id
	        pstmt.setString(2, reply.getPostID()); //Set the parent id
	        
	        // userName, body
	        pstmt.setString(3, reply.getUser());  //Set the user name   
	        pstmt.setString(4, reply.getBody()); //Set the header

	        pstmt.setString(5, reply.getCreateDate()); //Set the date created

	        pstmt.setBoolean(6, reply.getEditedBool()); //Set edited

	        String editedAt = reply.getEditedTime();    // null if never edited
	        if (editedAt == null || editedAt.isBlank()) {
	            pstmt.setNull(7, Types.VARCHAR);
	        } else {
	            pstmt.setString(7, editedAt);
	        }

	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	 }
	
	/*******
	 * <p>
	 * Method: void updateReply(Reply reply)
	 * </p>
	 * 
	 * <p>
	 * Description: Updates a reply and the relevant variables
	 * </p>
	 * 
	 * @param reply is the updated reply
	 */
	public void updateReply(Reply reply) {
		String query = "UPDATE replies SET body = ?, edited = ?, editedAt = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reply.getBody());
			pstmt.setBoolean(2, reply.getEditedBool());
			pstmt.setString(3, reply.getEditedTime());
			pstmt.setString(4, reply.getID());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: void deleteReply(Reply reply)
	 * </p>
	 * 
	 * <p>
	 * Description: Deletes a reply from the database
	 * </p>
	 * 
	 * @param reply is the reply to delete
	 */
	public void deleteReply(Reply reply) {
		String query = "DELETE FROM replies WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, reply.getID());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: List getUserList()
	 * </p>
	 * 
	 * <P>
	 * Description: Generate an List of Strings, one for each user in the database,
	 * starting with "<Select User>" at the start of the list.
	 * </p>
	 * 
	 * @return a list of userNames found in the database.
	 */
	public List<String> getUserList() {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
//		System.out.println(userList);
		return userList;
	}

	/*******
	 * <p>
	 * Method: boolean loginAdmin(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Check to see that a user with the specified username, password,
	 * and role is the same as a row in the table for the username, password, and
	 * role.
	 * </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the
	 *             Admin role.
	 * 
	 * @return true if the specified user has been logged in as an Admin else false.
	 * 
	 */
	public boolean loginAdmin(User user) {
		// Validates an admin user's login credentials so the user can login in as an
		// Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND " + "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next(); // If a row is returned, rs.next() will return true
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: boolean loginRole1(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Check to see that a user with the specified username, password,
	 * and role is the same as a row in the table for the username, password, and
	 * role.
	 * </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the
	 *             Student role.
	 * 
	 * @return true if the specified user has been logged in as an Student else
	 *         false.
	 * 
	 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND " + "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: boolean loginRole2(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Check to see that a user with the specified username, password,
	 * and role is the same as a row in the table for the username, password, and
	 * role.
	 * </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the
	 *             Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else
	 *         false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND " + "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: boolean doesUserExist(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Check to see that a user with the specified username is in the
	 * table.
	 * </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it
	 *                 is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	/*******
	 * <p>
	 * Method: int getNumberOfRoles(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Determine the number of roles a specified user plays.
	 * </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in
	 *             the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */
	// Get the number of roles that this user plays
	public int getNumberOfRoles(User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole())
			numberOfRoles++;
		if (user.getNewRole1())
			numberOfRoles++;
		if (user.getNewRole2())
			numberOfRoles++;
		return numberOfRoles;
	}

	/*******
	 * <p>
	 * Method: String generateInvitationCode(String emailAddress, String role)
	 * </p>
	 * 
	 * <p>
	 * Description: Given an email address and a roles, this method establishes and
	 * invitation code and adds a record to the InvitationCodes table. When the
	 * invitation code is used, the stored email address is used to establish the
	 * new user and the record is removed from the table.
	 * </p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role         specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely
	 *         setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
		String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
		String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	/*******
	 * <p>
	 * Method: int getNumberOfInvitations()
	 * </p>
	 * 
	 * <p>
	 * Description: Determine the number of outstanding invitations in the table.
	 * </p>
	 * 
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p>
	 * Method: boolean emailaddressHasBeenUsed(String emailAddress)
	 * </p>
	 * 
	 * <p>
	 * Description: Determine if an email address has been user to establish a user.
	 * </p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 * 
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();
			System.out.println(rs);
			if (rs.next()) {
				// Mark the code as used
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: String getRoleGivenAnInvitationCode(String code)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the role associated with an invitation code.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	/*******
	 * <p>
	 * Method: Role getRoleByUser(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the role associated with a username.
	 * </p>
	 * 
	 * @param username is the username to query
	 * 
	 * @return the role of that user
	 * 
	 */
	//For a given username return the role associated with that user
	public Role getRoleByUser(String username) 
	{
		String query = "SELECT adminRole, newRole1, newRole2 FROM userDB WHERE username = ?";      //Query All the Roles
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				if(rs.getBoolean("adminRole")) return Role.ADMIN; //If Admin Return Admin
				if(rs.getBoolean("newRole1")) return Role.NEWROLE1; //If New Role 1 Return New Role 1
				if(rs.getBoolean("newRole2")) return Role.NEWROLE2; //If New Role 2 Return New Role 2
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Role.NONE;
	}
	
	/*******
	 * <p>
	 * Method: boolean getActiveStatus(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the account's activity status
	 * </p>
	 * 
	 * @param username is the username to query
	 * 
	 * @return the active status of that user
	 * 
	 */
	//For a given username return the status of the account
	public boolean getActiveStatus(String username) 
	{
		String query = "SELECT accountActive FROM userDB WHERE username = ?";      //Query All the Roles
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("accountActive");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p>
	 * Method: void updateActiveStatus(String username, boolean active)
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the user's active status
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param active is the new status of the user
	 * 
	 */
	// update the active status
	public void updateActiveStatus(String username, boolean active) {
		String query = "UPDATE userDB SET accountActive = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, (active) ? "TRUE" : "FALSE");
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: boolean doesUserNeedPasswordReset(String username) 
	 * </p>
	 * 
	 * <p>
	 * Description: Determine if the user needs to reset their password
	 * </p>
	 * 
	 * @param username is the username to query
	 * 
	 * @return the role of that user
	 * 
	 */
	//For a given username return the status of the account
	public boolean doesUserNeedPasswordReset(String username) 
	{
		String query = "SELECT mustResetPass FROM userDB WHERE username = ?";      //Query All the Roles
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("mustResetPass");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p>
	 * Method: void updateUserPasswordReset(String username, boolean active)
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the status of the users need to reset their password
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param active is the new status of forced password reset
	 * 
	 */
	// update the active status
	public void updateUserPasswordReset(String username, boolean active) {
		String query = "UPDATE userDB SET mustResetPass = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, (active) ? "TRUE" : "FALSE");
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getEmailAddressUsingCode (String code )
	 * </p>
	 * 
	 * <p>
	 * Description: Get the email addressed associated with an invitation code.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty
	// string
	public String getEmailAddressUsingCode(String code) {
		String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p>
	 * Method: void removeInvitationAfterUse(String code)
	 * </p>
	 * 
	 * <p>
	 * Description: Remove an invitation record once it is used.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 */
	// Remove an invitation using an email address once the user account has been
	// setup
	public void removeInvitationAfterUse(String code) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int counter = rs.getInt(1);
				// Only do the remove if the code is still in the invitation table
				if (counter > 0) {
					query = "DELETE FROM InvitationCodes WHERE code = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
						pstmt2.setString(1, code);
						pstmt2.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	/*******
	 * <p>
	 * Method: String getFirstName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the first name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username
	 * 
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("firstName"); // Return the first name if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateFirstName(String username, String firstName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the first name of a user given that user's username and
	 * the new first name.
	 * </p>
	 * 
	 * @param username  is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 * 
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
		String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentFirstName = firstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getMiddleName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the middle name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username
	 * 
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("middleName"); // Return the middle name if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateMiddleName(String username, String middleName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the middle name of a user given that user's username and
	 * the new middle name.
	 * </p>
	 * 
	 * @param username   is the username of the user
	 * 
	 * @param middleName is the new middle name for the user
	 * 
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
		String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, middleName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentMiddleName = middleName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getLastName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the last name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username
	 * 
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("lastName"); // Return last name role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateLastName(String username, String lastName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the middle name of a user given that user's username and
	 * the new middle name.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param lastName is the new last name for the user
	 * 
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
		String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentLastName = lastName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getPreferredFirstName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the preferred first name of a user given that user's
	 * username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username
	 * 
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("preferredFirstName"); // Return the preferred first name if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updatePreferredFirstName(String username, String
	 * preferredFirstName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the preferred first name of a user given that user's
	 * username and the new preferred first name.
	 * </p>
	 * 
	 * @param username           is the username of the user
	 * 
	 * @param preferredFirstName is the new preferred first name for the user
	 * 
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
		String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPreferredFirstName = preferredFirstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getEmailAddress(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the email address of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username
	 * 
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("emailAddress"); // Return the email address if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateEmailAddress(String username, String emailAddress)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the email address name of a user given that user's
	 * username and the new email address.
	 * </p>
	 * 
	 * @param username     is the username of the user
	 * 
	 * @param emailAddress is the new preferred first name for the user
	 * 
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
		String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentEmailAddress = emailAddress;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: boolean updatePassword(String username, String password)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the password of a user given that user's
	 * username and the new password.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 * 
	 */
	// get the attributes for a specified user
	public void updatePassword(String username, String password) {
		String query = "UPDATE userDB SET password = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPassword = password;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*Hw3....lines--------------------------------> */
	public boolean updatePasswordAuthorized(String targetUsername, String actingUsername, String newPassword) {
	    if (connection == null || targetUsername == null || actingUsername == null || newPassword == null) {
	        return false;
	    }

	    try {
	        if (!userExists(targetUsername)) {
	            return false;
	        }

	        boolean actingOwnAccount = targetUsername.equals(actingUsername);
	        if (!actingOwnAccount && !isAdminUser(actingUsername)) {
	            return false;
	        }

	        String query = "UPDATE userDB SET password = ? WHERE username = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setString(1, newPassword);
	            pstmt.setString(2, targetUsername);
	            int updated = pstmt.executeUpdate();
	            if (updated > 0 && targetUsername.equals(currentUsername)) {
	                currentPassword = newPassword;
	            }
	            return updated > 0;
	        }
	    } catch (SQLException e) {
	        return false;
	    }
	}

	public boolean updateUserRoleAuthorized(String targetUsername, String actingUsername, Role role, boolean newValue) {
	    if (connection == null || targetUsername == null || actingUsername == null || role == null) {
	        return false;
	    }

	    try {
	        if (!userExists(targetUsername)) {
	            return false;
	        }

	        if (!isAdminUser(actingUsername)) {
	            return false;
	        }

	        return updateUserRole(targetUsername, role, Boolean.toString(newValue));
	    } catch (SQLException e) {
	        return false;
	    }
	}

	public boolean isCurrentUserAdmin() {
	    return currentAdminRole;
	}
	 public void deleteUserAccount(String username) {
         if (connection == null || username == null) {
                 return;
         }

         String query = "DELETE FROM userDB WHERE username = ?";
         try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                 pstmt.setString(1, username);
                 pstmt.executeUpdate();
         } catch (SQLException e) {
                 // Intended for test cleanup;
         }
 }
	private boolean userExists(String username) throws SQLException {
	    String query = "SELECT 1 FROM userDB WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next();
	        }
	    }
	}

	private boolean isAdminUser(String username) throws SQLException {
	    String query = "SELECT adminRole FROM userDB WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next() && rs.getBoolean(1);
	        }
	    }
	}
	
	/*-----------------h3lines*/
	
	
	
	/*******
	 * <p>
	 * Method: boolean getUserAccountDetails(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get all the attributes of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 * 
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			currentUsername = rs.getString(2);
			currentPassword = rs.getString(3);
			currentFirstName = rs.getString(4);
			currentMiddleName = rs.getString(5);
			currentLastName = rs.getString(6);
			currentPreferredFirstName = rs.getString(7);
			currentEmailAddress = rs.getString(8);
			currentAdminRole = rs.getBoolean(9);
			currentNewRole1 = rs.getBoolean(10);
			currentNewRole2 = rs.getBoolean(11);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * <p>
	 * Method: boolean updateUserRole(String username, String role, String value)
	 * </p>
	 * 
	 * <p>
	 * Description: Update a specified role for a specified user's and set and
	 * update all the current user attributes.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param role     is string that specifies the role to update
	 * 
	 * @param value    is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 * 
	 */
	// Update a users role
	public boolean updateUserRole(String username, Role role, String value) {
		String query = "";
		switch (role) 
		{
			case Role.ADMIN:
				query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentAdminRole = true;
					else
						currentAdminRole = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
		case Role.NEWROLE1:
				query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentNewRole1 = true;
					else
						currentNewRole1 = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
			case Role.NEWROLE2:
				query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentNewRole2 = true;
					else
						currentNewRole2 = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
			default:
				return false;
		}
	}

	// Attribute getters for the current user
	/*******
	 * <p>
	 * Method: String getCurrentUsername()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's username.
	 * </p>
	 * 
	 * @return the username value is returned
	 * 
	 */
	public String getCurrentUsername() {
		return currentUsername;
	};

	/*******
	 * <p>
	 * Method: String getCurrentPassword()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's password.
	 * </p>
	 * 
	 * @return the password value is returned
	 * 
	 */
	public String getCurrentPassword() {
		return currentPassword;
	};

	/*******
	 * <p>
	 * Method: String getCurrentFirstName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's first name.
	 * </p>
	 * 
	 * @return the first name value is returned
	 * 
	 */
	public String getCurrentFirstName() {
		return currentFirstName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentMiddleName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's middle name.
	 * </p>
	 * 
	 * @return the middle name value is returned
	 * 
	 */
	public String getCurrentMiddleName() {
		return currentMiddleName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentLastName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's last name.
	 * </p>
	 * 
	 * @return the last name value is returned
	 * 
	 */
	public String getCurrentLastName() {
		return currentLastName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentPreferredFirstName(
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's preferred first name.
	 * </p>
	 * 
	 * @return the preferred first name value is returned
	 * 
	 */
	public String getCurrentPreferredFirstName() {
		return currentPreferredFirstName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentEmailAddress()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's email address name.
	 * </p>
	 * 
	 * @return the email address value is returned
	 * 
	 */
	public String getCurrentEmailAddress() {
		return currentEmailAddress;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentAdminRole()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Admin role attribute.
	 * </p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 * 
	 */
	public boolean getCurrentAdminRole() {
		return currentAdminRole;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentNewRole1()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Student role attribute.
	 * </p>
	 * 
	 * @return true if this user plays a Student role, else false
	 * 
	 */
	public boolean getCurrentNewRole1() {
		return currentNewRole1;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentNewRole2()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Reviewer role attribute.
	 * </p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 * 
	 */
	public boolean getCurrentNewRole2() {
		return currentNewRole2;
	};

	/*******
	 * <p>
	 * Debugging method
	 * </p>
	 * 
	 * <p>
	 * Description: Debugging method that dumps the database of the console.
	 * </p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
			for (int i = 0; i < meta.getColumnCount(); i++) {
				System.out.println(meta.getColumnLabel(i + 1) + ": " + resultSet.getString(i + 1));
			}
			System.out.println();
		}
		resultSet.close();
	}

	/*******
	 * <p>
	 * Method: void closeConnection()
	 * </p>
	 * 
	 * <p>
	 * Description: Closes the database statement and connection.
	 * </p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
