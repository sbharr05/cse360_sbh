package entityClasses;

import java.time.*;
import java.time.format.*;

/*******
 * <p>
 * Title: CreateInfo
 * </p>
 * 
 * <p>
 * Description: This class is a container for creation and edit info. It stores creation user, creation date, edit status, edit date, and a unique id.
 * It is utilized by multiple classes inside the entityClasses package to functionally store information in reference to an objects creation and edit status.
 * </p>
 * 
 * 
 * @author Sutton Harr 
 * 
 * @version 1.03 
 * 
 */

public class CreateInfo {
	private String username; //User name of the post creator
	private String dateCreated; //The time in which the post was created (Filled on creation of new object)
	private boolean edited = false;   //Boolean to determine if the post has been edited
	private String dateEdited = null; //The time in which the post was last edited (Filled every time edit function is called)
	private String Id; //Unique Identifier for parsing lists of replies/posts
	
	
	/**
	 * Constructor Method
	 * Takes user name and sets relevant parameters
	 * Captures current system time to be stored for display later
	 * 
	 * @param username name of the user who created this object
	 */
	public CreateInfo(String username) 
	{
		this.username = username;        //Set post user name
		dateCreated = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault()).format(Instant.now());     //Capture current system time
		Id = java.util.UUID.randomUUID().toString(); //Create id for the post
	}
	
	/**
	 * Full Constructor Method for SQL mapping
	 * 
	 * @param username name of creator
	 * @param dateCreated the date in which this object was created
	 * @param edited the edit status of this object
	 * @param dateEdited the date this object was edited (if at all)
	 * @param Id the unique identifier for this object
	 */
	public CreateInfo(String username, String dateCreated, boolean edited, String dateEdited, String Id) 
	{
		this.username = username;
		this.dateCreated = dateCreated;
		this.edited = edited;
		this.dateEdited = dateEdited;
		this.Id = Id;
	}
	

	/**
	 * Function to be called when a post is edited
	 * Sets edited boolean to true, returns the state of edited before update
	 * False if the post has not yet edited, and true if it already has
	 * 
	 * @return true if this if this object has already been edited, false if this is the first edit
	 */
	public boolean edit() 
	{
		boolean toReturn = edited;  //Get value of edited before update
		dateEdited = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault()).format(Instant.now()); //Capture current time
		edited = true; //Set edited to true
		return toReturn; //Return if the post has been edited already
	}
	
	
	//Getter Methods
	
	/**
	 * Gets the user name of the creator
	 * 
	 * @return user name of creator as a string
	 */
	public String getUser() {return username;}
	
	/**
	 * Creation date getter method
	 * 
	 * @return a formatted output of the creation date as a string
	 */
	public String getCreationDate() {return dateCreated;}

	/**
	 * Edited bool getter method
	 * 
	 * @return boolean of whether or not this object has been edited
	 */
	public boolean getEdited() { return edited;}
	
	/**
	 * Last Edited Date Getter Method
	 * 
	 * @return a formatted version of the last edit as a string
	 */
	public String getLastEditDate() {return dateEdited;}
	
	/**
	 * ID Getter Method
	 * 
	 * @return the unique string identifier for this object
	 */
	public String getID() {return Id;}
}
