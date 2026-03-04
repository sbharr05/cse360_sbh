package guiDiscussionBoard;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import customGuiClasses.TwoInputDialog;
import customGuiClasses.TwoInputDialog.TwoStringResults;
import customGuiClasses.TextAreaDialog;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p>
 * Title: ControllerDiscussionBoard
 * </p>
 * 
 * <p>
 * Description: This class is the controller for the discussion board, and is instantiated by the view
 * in order to carry out operations conducted by the UI buttons created in the view. It's methods are non static and are
 * heavily utilized by the view class
 * </p>
 * 
 * 
 * @author Sutton Harr
 * @version 1.1
 * @see ViewDiscussionBoard
 * 
 * 
 */
public class ControllerDiscussionBoard {
	private static Database db; //The main database
	private ArrayList<Post> posts = new ArrayList<Post>(); //A list of every discussion post
	private HashMap<String, ArrayList<Reply>> replies = new HashMap<String, ArrayList<Reply>>(); //A hash map which uses the parent post id as the key for a list of all replies under that post
	
	/**
	 * Fills out the list Variables for the list of posts on instantiation
	 */
	public ControllerDiscussionBoard() 
	{
		db = FoundationsMain.database; //Set database
		posts = db.getDiscussionPosts(); //Get all posts
		replies = db.getPostReplies(); //Get all replies
		
	}
	
	
	/**
	 * Getter Method for the list of posts stored in by this object
	 * 
	 * @return the list of Post Objects
	 */
	public List<Post> listPosts()
	{
		return posts; //Return list of posts
	}
	
	/**
	 * This method is used to get all replies that are linked to a post based on the post's unique ID
	 * 
	 * @param postId the unique ID stored by the desired post object
	 * @return an array list of replies associated with that post's ID
	 */
	public ArrayList<Reply> getPostReplies(String postId)
	{
		return replies.get(postId);
	}
	
	
	/**
	 * Determines if a user has permissions to edit a post
	 * Provides an error message if not silenced, the content of the message is based on the delete boolean
	 * 
	 * @param post the post object in which the current users credentials must be validated for
	 * @param delete boolean to determine the content of the error message (True "delete" : False "edit")
	 * @param silenceNotification boolean to determine if an error message should be displayed upon invalid credentials
	 * @return a boolean indicating if the current user has the authority to edit the post parameter
	 */
	public boolean canEdit(Post post, boolean delete, boolean silenceNotification) 
	{
		boolean allowed = db.getCurrentAdminRole() || post.getUser() == db.getCurrentUsername(); //Determine permission
		
		if(allowed) return true; //Return True
		else 
		{
			
			//If not silenced create a warning so the user knows they can't complete the operation
			if(!silenceNotification) warnInvalidAction((delete) ? "delete" : "edit", "post");
			return false;
		}
	}
	
	/**
	 * Determines if a user has permissions to edit a reply
	 * Provides an error message if not silenced, the content of the message is based on the delete boolean
	 * 
	 * @param reply the object reply in which the current users credentials must be validated for
	 * @param delete boolean to determine the content of the error message (True "delete" : False "edit")
	 * @param silenceNotification boolean to determine if an error message should be displayed upon invalid credentials
	 * @return a boolean indicating if the current user has the authority to edit the reply parameter
	 */
	public boolean canEdit(Reply reply, boolean delete, boolean silenceNotification) 
	{
		boolean allowed = db.getCurrentAdminRole() || reply.getUser() == db.getCurrentUsername();
		
		if(allowed) return true;
		else 
		{
			if(!silenceNotification) warnInvalidAction((delete) ? "delete" : "edit", "reply");
			return false;
		}
	}
	
	/**
	 * Creates a waring based on the input reason and type
	 * 
	 * @param reason string indicating the attempted operation
	 * @param type string indicating the type of entityClass in which the operation was attempted
	 */
	private void warnInvalidAction(String reason, String type) 
	{
		Alert warning = new Alert(AlertType.ERROR);
		warning.setTitle("Invalid Credentials");
		warning.setHeaderText("Only an Admin or the Original Poster can " + reason + " this " + type + "!");
		
		warning.showAndWait();
	}
	
	/**
	 * Prompts user to create a new post then saves the new post to the database
	 * Input validation is handled internally, void if cancelled or invalid.
	 */
	public void createPost() 
	{
		//Create a new Two Input Dialog
		TwoInputDialog newPostDialog = new TwoInputDialog("New Discussion Post", "Create a new discussion post.", "Header", "Body");
		
		//Setup some visual elements of the input
		newPostDialog.changeTextFieldSizes(600, -1, 600, 300);
		newPostDialog.setMaxCharsForFields(100, 1000); //No Input Validation needed as a max char count is forced here
		newPostDialog.getTextField2().setWrapText(true);
		
		
		Optional<TwoStringResults> postCreateResults; //Optional to contain the results
		
		postCreateResults = newPostDialog.showAndWait(); //Show the Two input dialog
		
		//If the user input is not empty, or they did not hit cancel create new post
		if (postCreateResults.isPresent() && !postCreateResults.get().getText1().equals("") && !postCreateResults.get().getText2().equals("")) 
		{
			//Create new Post object
			Post newPost = new Post(db.getCurrentUsername(), postCreateResults.get().getText1(), postCreateResults.get().getText2());
			
			db.createPost(newPost); //Save to database
			posts.addFirst(newPost); //Add to top of the list
		} else if (postCreateResults.isPresent())
		{
			Alert invalidInput = new Alert(AlertType.ERROR);
			boolean header = postCreateResults.get().getText1().equals("");
			boolean body = postCreateResults.get().getText2().equals("");
			invalidInput.setHeaderText("Post Cannot Be Created");
			invalidInput.setTitle("Invalid Operation");
			
			if (header & body) 
			{
				invalidInput.setContentText("Your post must contain a header and a body");
			} else if (header) 
			{
				invalidInput.setContentText("Your post must contain a header");
			} else if (body) 
			{
				invalidInput.setContentText("Your post must contain a body");
			}
			
			invalidInput.showAndWait();
		}
	}
	
	/**
	 * Prompts the user to edit a post, then saves edits to object and database.
	 * Input Validation handled internally, if no changes are made or changes are invalid no operation is conducted
	 * 
	 * @param post the post object to be edited
	 * @return a boolean value indicating if the post was edited successfully
	 */
	public boolean editPost(Post post) 
	{
		//Create a new Two Input Dialog
		TwoInputDialog newPostDialog = new TwoInputDialog("Edit Discussion Post", "Please Make Changes to Post", "Header", "Body");
		
		//Set the fields to the previous values
		newPostDialog.getTextField1().setText(post.getHeader());
		newPostDialog.getTextField2().setText(post.getBody());
		
		//Setup some visual elements of the input
		newPostDialog.changeTextFieldSizes(600, -1, 600, 300);
		newPostDialog.setMaxCharsForFields(100, 1000); //No Input Validation needed as a max char count is forced here
		newPostDialog.getTextField2().setWrapText(true);
		
		
		Optional<TwoStringResults> postEditResults; //Optional to contain the results
		
		postEditResults = newPostDialog.showAndWait(); //Show the Two input dialog
		
		//If the user input is not empty, or they did not hit cancel create new post
		if (postEditResults.isPresent() && !postEditResults.get().getText1().equals("") && !postEditResults.get().getText2().equals("") && !(postEditResults.get().getText1().equals(post.getHeader()) && postEditResults.get().getText2().equals(post.getBody()))) 
		{
			post.editPost(postEditResults.get().getText1(), postEditResults.get().getText2());
			db.updatePost(post);
			return true;
		}
		else if (postEditResults.isPresent())
		{
			Alert invalidInput = new Alert(AlertType.ERROR);
			boolean header = postEditResults.get().getText1().equals("");
			boolean body = postEditResults.get().getText2().equals("");
			invalidInput.setHeaderText("Post Was Not Edited");
			invalidInput.setTitle("Invalid Operation");
			
			if (header & body) 
			{
				invalidInput.setContentText("Your post must contain a header and a body");
			} else if (header) 
			{
				invalidInput.setContentText("Your post must contain a header");
			} else if (body) 
			{
				invalidInput.setContentText("Your post must contain a body");
			}
			
			invalidInput.showAndWait();
		}
		
		return false;
	}
	
	/**
	 * Prompts a user if they want to delete a post and deletes it from the database if confirmed.
	 * If user elects to cancel the deletion no operation occurs.
	 * 
	 * @param post post object to be deleted
	 * @return a boolean indicating the success of the operation
	 */
	public boolean deletePost(Post post) 
	{
		
		//Create new alert
		Alert confirm = new Alert(AlertType.CONFIRMATION,"This action is PERMANENT and CANNOT be undone! Continue?", ButtonType.YES, ButtonType.CANCEL);
		
		//Set content of alert
		confirm.setTitle("Delete Post");
		confirm.setHeaderText("Delete Post?");
		
		Optional<ButtonType> selection = confirm.showAndWait(); //Result optional of the alert
		
		//If the user there was a selection
		if (selection.isPresent()) 
		{
			//If yes it pressed
			if (selection.get() == ButtonType.YES) 
			{
				db.deletePost(post); //Delete from db
				posts.remove(post); //Remove from posts list
				replies.remove(post.getID()); //Remove all replies too
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Warns the user of resolved status consequences then toggles the resolved status of a post when confirmed
	 * Updates both the object and the database
	 * 
	 * @param post post object to be to change resolved status
	 */
	public void toggleResolved(Post post) 
	{
		//Alert for confirmation
		Alert confirm = new Alert(AlertType.CONFIRMATION, (post.getSolved()) ? "This action will re-open the discussion for replies." : "This action will close the discussion, when closed it cannot recieve any new replies or edits.", ButtonType.YES, ButtonType.CANCEL);
		
		//Set alert content
		confirm.setTitle((post.getSolved()) ? "Re-Open Discussion" : "Mark Resolved");
		confirm.setHeaderText((post.getSolved()) ? "Re-Open Discussion?" : "Mark as Resolved?");
		
		Optional<ButtonType> selection = confirm.showAndWait();
		
		if (selection.isPresent()) 
		{
			//If user presses yes change the status and update the post
			if (selection.get() == ButtonType.YES) 
			{
				post.markAsSolved(!post.getSolved());
				db.updatePostSolvedStatus(post);
			}
		}
	}
	
	/**
	 * Prompts user to create a new reply then saves the new post to the database
	 * Input validation is handled internally, void if cancelled or invalid.
	 * 
	 * @param postId the unique identifier of the post which this reply is associated with
	 */
	public void createReply(String postId) 
	{
		
		//Create a text area dialog
		TextAreaDialog replyDialog = new TextAreaDialog("Create New Reply", "Please Enter Your Reply", "Body");
		
		//Set up dialog
		replyDialog.changeTextAreaSize(500, 200);
		replyDialog.getTextArea().setWrapText(true);
		replyDialog.setMaxChars(300); //Force max chars
		
		Optional<String> replyCreateResults = replyDialog.showAndWait(); //Results
		
		//If they input a new reply that is not empty
		if(replyCreateResults.isPresent() && !replyCreateResults.get().equals("")) 
		{
			Reply newReply = new Reply(db.getCurrentUsername(), replyCreateResults.get(), postId);
			
			//Save it to the database and add to relevant lists
			db.createReply(newReply);
			replies.putIfAbsent(postId, new ArrayList<Reply>());
			replies.get(postId).add(newReply);
		}else if (replyCreateResults.isPresent())
		{
			Alert invalidInput = new Alert(AlertType.ERROR, "Your reply must contain text.");
			invalidInput.setHeaderText("Reply Could Not Be Created");
			invalidInput.setTitle("Invalid Operation");
			invalidInput.showAndWait();
		}
	}
	
	/**
	 * Prompts the user to edit a reply, then saves edits to object and database.
	 * Input Validation handled internally, if no changes are made or changes are invalid no operation is conducted
	 * 
	 * @param reply the reply object to be edited
	 */
	public void editReply(Reply reply) 
	{
		TextAreaDialog replyDialog = new TextAreaDialog("Edit Reply", "Edit Your Reply", "Body");
		
		replyDialog.getTextArea().setText(reply.getBody()); //Set the reply input to the content of the old reply
		replyDialog.getTextArea().setWrapText(true);
		replyDialog.changeTextAreaSize(500, 200);
		replyDialog.setMaxChars(300); //Force max chars
		
			
		Optional<String> replyEditResults = replyDialog.showAndWait();
			
		if(replyEditResults.isPresent() && !replyEditResults.get().equals(reply.getBody()) && !replyEditResults.get().equals("")) 
		{
			//If new update the reply
			reply.editReply(replyEditResults.get());
			db.updateReply(reply);
		} else if (replyEditResults.isPresent())
		{
			Alert invalidInput = new Alert(AlertType.ERROR, "Your reply must contain text.");
			invalidInput.setHeaderText("Reply Was Not Edited");
			invalidInput.setTitle("Invalid Operation");
			invalidInput.showAndWait();
		}
	}
	
	
	/**
	 * Prompts a user if they want to delete a reply and deletes it from the database if confirmed.
	 * If user elects to cancel the deletion no operation occurs.
	 * 
	 * @param reply reply object to be deleted
	 */
	public void deleteReply(Reply reply) 
	{
		//Create alert to confirm deletion
		Alert confirm = new Alert(AlertType.CONFIRMATION,"This action is PERMANENT and CANNOT be undone! Continue?", ButtonType.YES, ButtonType.CANCEL);
		
		//Set up alert content
		confirm.setTitle("Delete Reply");
		confirm.setHeaderText("Delete Reply?");
		
		Optional<ButtonType> selection = confirm.showAndWait(); //Results
		
		if (selection.isPresent()) 
		{
			//If user selects yes, delete the reply from database and remove from replies list
			if (selection.get() == ButtonType.YES) 
			{
				db.deleteReply(reply);
				replies.get(reply.getPostID()).remove(reply);
			}
		}
	}
	
	
	

}
