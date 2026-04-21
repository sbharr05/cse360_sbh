package entityClasses;

public class Reply implements DiscussionBoardContent{
	private CreateInfo replyInfo; //Info class to store information about the post
	private String body; //The body text of the post
	private String postID;
	
	
	//Constructor for new post
	public Reply(String user, String body, String postID) 
	{
		replyInfo = new CreateInfo(user); //Creates user info object
		this.body = body; //Stores the input body
		this.postID = postID;
	}
	
	//Full Constructor for creating replies from SQL table
	public Reply(CreateInfo info, String body, String postID) 
	{
		this.replyInfo = info;
		this.body = body;
		this.postID = postID;
	}
	
	//Edit post method, captures edit status
	//Returns a boolean based on if the post was changed
	public boolean editReply(String newBody) 
	{
		if (newBody == "") return false; //If both input are empty do not change the post, will return false
		
		body = (newBody != "") ? newBody : body; ///If there is no input for a new body keep the body the same
		
		replyInfo.edit(); //Save latest post edit time
		return true; //Return true for edit complete
	}
	
	
	//Getter Methods
	public String getCreateDate() {return replyInfo.getCreationDate();} //Returns a string of the date the post was created
	public String getBody() {return body;}
	public String getUser() {return replyInfo.getUser();}
	public String getID() {return replyInfo.getID();}
	public String getPostID() {return postID;}
	public String getEditedTime() {return replyInfo.getLastEditDate();}
	public boolean getEditedBool() {return replyInfo.getEdited();}
	
	//If the post has been edited return a string displaying last edit date else return nothing
	public String getCreateInfoFormatted() 
	{
			if(replyInfo.getEdited()) return getCreateDate() + " (Edited: " + replyInfo.getLastEditDate() + ")";
			else return getCreateDate();
	}
	
	//Summary of the reply to be displayed and implement the container interface
	public String getSummary() 
	{
		String sub = body.substring(0, Math.min(body.length(), 30));
		if(sub.length() != body.length()) sub += "...";
		return sub;
	}
}
