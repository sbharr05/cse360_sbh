package entityClasses;

/*******
 * <p>
 * Title: Post
 * </p>
 * 
 * <p>
 * Description: This class is a container for post entities within the system, it contains all relevant post information including
 * CreateInfo, header, body, and solved status. It also containers several helper functions to utilize this data in accordance with
 * the services provided in the application.
 * </p>
 * 
 * 
 * @author Sutton Harr 
 * 
 * @version 1.03 
 * @see CreateInfo
 * 
 * 
 */
public class Post implements DiscussionBoardContent{
	private CreateInfo postInfo; //Info class to store information about the post
	private String header; //The header text of the post
	private String body; //The body text of the post
	private boolean solved = false; //To Determine if the post is solved
	
	
	/**
	 * Constructor for new post
	 * 
	 * @param user the user who created the post
	 * @param header the header of the post
	 * @param body the body text of the post
	 */
	public Post(String user, String header, String body) 
	{
		postInfo = new CreateInfo(user); //Creates user info object
		this.header = header; //Stores the input header
		this.body = body; //Stores the input body
	}
	
	
	/**
	 * Full constructor method for building from SQL Save
	 * 
	 * @param info the relevant create info of the post
	 * @param header the header of the post
	 * @param body the body of the post
	 * @param solved the solved status of the post
	 */
	public Post(CreateInfo info, String header, String body, boolean solved) 
	{
		this.postInfo = info;
		this.header = header;
		this.body = body;
		this.solved = solved;
	}
	
	/**
	 * Edit post method, captures edit status
	 * 
	 * @param newHeader the new header value of the post 
	 * @param newBody the new body of the post
	 * @return a boolean based on if the post was changed
	 */
	public boolean editPost(String newHeader, String newBody) 
	{
		if (newHeader == "" && newBody == "") return false; //If both input are empty do not change the post, will return false
		
		header = (newHeader != "") ? newHeader : header;  //If no input for a new header then it will will not change anything
		body = (newBody != "") ? newBody : body; ///If there is no input for a new body keep the body the same
		
		postInfo.edit(); //Save latest post edit time
		return true; //Return true for edit complete
	}
	
	
	/**
	 * Marks an instance of a post as solved
	 * 
	 * @param solved the new solved status of the post
	 */
	public void markAsSolved(boolean solved) {this.solved = solved;}
	
	
	//Getter Methods
	
	
	/**
	 * Gets the creation date of the post formatted as a string
	 * 
	 * @return the creation date of the post as a string
	 */
	public String getCreateDate() {return postInfo.getCreationDate();} //Returns a string of the date the post was created
	
	
	/**
	 * Gets the unique ID of the post
	 * 
	 * @return the unique post ID
	 */
	public String getID() {return postInfo.getID();}
	
	
	/**
	 * Get's the edit status of the post
	 * 
	 * @return the boolean edit status of the post
	 */
	public boolean getEdited() {return postInfo.getEdited();}
	
	
	/**
	 * Get's the last time the post was edited as a string
	 * 
	 * @return string containing a formatted version of the last edit time
	 */
	public String getEditedTime() {return postInfo.getLastEditDate();}
	
	
	/**
	 * Get's the user who created the post
	 * 
	 * @return the user name of the poster
	 */
	public String getUser() {return postInfo.getUser();}
	
	
	/**
	 * Gets the header of the post
	 * 
	 * @return a string containing the post header
	 */
	public String getHeader() {return header;}
	
	
	/**
	 * Gets the body of the post
	 * 
	 * @return a string containing the body of the post
	 */
	public String getBody() {return body;} 
	
	
	
	/**
	 * Gets the solved status of the post
	 * 
	 * @return a boolean indicating solved status
	 */
	public boolean getSolved() {return solved;}
	
	
	/**
	 * Helper Method to determine what to display next to the header when a post is solved
	 * 
	 * @return a string indicating the solved status of the post, empty if not marked as solved
	 */
	public String getSolvedText() {if(solved) return " (SOLVED)"; else return "";}
	
	
	/**
	 * Returns the complete creation info of the post (Create + Edit Dates)
	 * If the post has been edited return a string displaying last edit date else return nothing
	 * 
	 * @return a String containing the formatted creation and edit dates
	 */
	public String getCreateInfoFormatted() 
	{
		if(postInfo.getEdited()) return getCreateDate() + " (Edited: " + postInfo.getLastEditDate() + ")";
		else return getCreateDate();
	}
	
	
	/**
	 * Returns the header in order to be displayed in short and implement the 
	 * container interface
	 * 
	 * @return a String containing the header
	 */
	public String getSummary() 
	{
		return header;
	}
}
