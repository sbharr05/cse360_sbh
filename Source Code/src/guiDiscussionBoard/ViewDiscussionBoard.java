package guiDiscussionBoard;

import entityClasses.Post;
import entityClasses.Reply;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.List;


/*******
 * <p>
 * Title: ViewDiscussionBoard
 * </p>
 * 
 * <p>
 * Description: This class creates and setups the required UI and UI interactions for the operation of the discussion board.
 * It creates a new instance of the controller as to load required information for the setup of the page.
 * </p>
 * 
 * 
 * @author Sutton Harr
 * @version 1.1
 * @see ControllerDiscussionBoard
 * 
 * 
 */
public class ViewDiscussionBoard {
	
	
    private final TableView<Post> postsTable = new TableView<>(); //The table of all the posts
    private final TableView<Reply> repliesTable = new TableView<>(); //The table of all the replies
    private ControllerDiscussionBoard controller = new ControllerDiscussionBoard(); //Instance of the controller class

	private Post activePost; //Currently Active Post
	private Label label_Header = new Label(); //Label for the post header when viewing
	private TextArea post_Body = new TextArea(); //Label for the Text Area of the body content
	private Stage parentStage; //The Stage of the post list
	private Stage viewStage; //The Stage of the post view window
	private HBox filters; //The filters for the posts
    
    /**
     * Create new instance and display
     * 
     * @param owner the parent window of the discussion board
     */
    public static void display(Stage owner) {
        new ViewDiscussionBoard().show(owner);
    }

    /**
     * Initializes the base view of the discussion board
     * 
     * @param owner the parent window of the discussion board
     */
    @SuppressWarnings("unchecked")
	private void show(Stage owner) {
        Stage dlg = new Stage(); //New Stage
        parentStage = dlg;
        
        //Init the window things
        dlg.initOwner(owner);
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.setTitle("Discussion Posts");
        postsTable.setPlaceholder(new Label("No Posts to Display"));
        

	     // Top bar (search + solved filter)
	     TextField search = new TextField(); //New Text Field
	     search.setPromptText("Search Posts...");
	     search.setOnKeyTyped(e -> refresh()); //When a key is typed into field refresh the list
	     
	     ComboBox<String> solvedFilter = new ComboBox<>(FXCollections.observableArrayList("All", "Solved" ,"Unsolved")); //Combo Box to sort by the solved status of the post
	     solvedFilter.getSelectionModel().selectFirst();
	     solvedFilter.setOnAction(e -> refresh()); //When a new filter is selected refresh
	
	     filters = new HBox(8, new Label("Search:"), search, new Label("Status:"), solvedFilter); //Set the filters var
	     filters.setPadding(new Insets(10));
	     filters.setAlignment(Pos.CENTER_LEFT);
	     
        
        
        // User ID
        TableColumn<Post, String> colDate = new TableColumn<>("Date Published");
        colDate.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCreateInfoFormatted()));
        colDate.setPrefWidth(150);

        // Display Name
        TableColumn<Post, String> colName = new TableColumn<>("Posted By");
        colName.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUser()));
        colName.setPrefWidth(70);

        // Role (keep as lambda)
        TableColumn<Post, String> colHead = new TableColumn<>("Header");
        colHead.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getHeader() + c.getValue().getSolvedText()));
        colHead.setPrefWidth(200);

        // Active (keep as lambda)
        TableColumn<Post, String> colSum = new TableColumn<>("Summary");
        colSum.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getBody()));
        colSum.setPrefWidth(400);

        postsTable.getColumns().addAll(colDate, colName, colHead, colSum);
        postsTable.setPrefHeight(360);
        postsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        
        // Bottom: actions
        Button btnCreate    = new Button("Create");
        Button btnEdit = new Button("Edit");
        Button btnView  = new Button("View");
        Button btnDelete  = new Button("Delete");
        Button btnClose       = new Button("Close");

        //Set all relevant button actions
        btnCreate.setOnAction(e -> onCreate());
        btnView.setOnAction(e -> viewPost());
        btnClose.setOnAction(e -> dlg.close());
        btnEdit.setOnAction(e -> onEdit1());
        btnDelete.setOnAction(e -> onDelete1());

        
        //Compile list of all buttons
        HBox actions = new HBox(10, btnCreate, btnView, btnEdit, btnDelete, new Region(), btnClose);
        HBox.setHgrow(actions.getChildren().get(actions.getChildren().size()-2), Priority.ALWAYS);
        actions.setPadding(new Insets(10));

        
        BorderPane root = new BorderPane();
        
        root.setCenter(new VBox(5, postsTable)); //Set table in the middle
        root.setBottom(actions); //Set actions at the bottom
        root.setTop(filters); //Set Filters at the top

        Scene scene = new Scene(root, 820, 520);
        dlg.setScene(scene);
        

        // Initial load
        refresh();

        dlg.showAndWait();
    }
    


    /**
     * Creates a new window and initializes the relevant components for displaying a post from the discussion board.
     * Once initialized the window is shown to the user.
     * 
     * @param post the post to be displayed
     */
    @SuppressWarnings("unchecked")
	private void showPost(Post post) {
    	activePost = post; //Set currently viewed post
    	
    	//Create new window stage
        viewStage = new Stage();
        viewStage.initOwner(parentStage);
        viewStage.initModality(Modality.WINDOW_MODAL);
        viewStage.setTitle(post.getHeader() + " - " + post.getCreateInfoFormatted());
        
        //Set up Header
        label_Header.setText(post.getHeader() + post.getSolvedText());
        setupLabelUI(label_Header, "Arial", 28, 350, Pos.TOP_LEFT, 0, 5);
        
        
        //Set up Body
        post_Body.setText(post.getBody());
        post_Body.setWrapText(true);
        post_Body.setEditable(false);
        
        //Set the style of the body (It had this weird blue outline I needed to get rid of)
        post_Body.setStyle(
        	    "-fx-focus-color: transparent;" +
        	    "-fx-faint-focus-color: transparent;" +
        	    "-fx-background-color: transparent;" +
        	    "-fx-background-insets: 0;" +
        	    "-fx-background-radius: 0;" +
        	    "-fx-border-color: transparent;"
        	);
        
        //Clear replies table
        repliesTable.getColumns().clear();
        
        //Date Published
        TableColumn<Reply, String> colDate = new TableColumn<>("Date Published");
        colDate.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCreateInfoFormatted()));
        colDate.setPrefWidth(100);

        // Display Name
        TableColumn<Reply, String> colName = new TableColumn<>("Posted By");
        colName.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUser()));
        colName.setPrefWidth(70);

        //Content
        TableColumn<Reply, String> colBody = new TableColumn<>("Reply");
        colBody.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getBody()));
        colBody.setPrefWidth(400);
        
        //This sets it so that the reply content will wrap so that it can be read better
        //It creates a new type of table cell to be used for the content
        colBody.setCellFactory(tc -> new TableCell<Reply, String>() {
        	
        	//New Text
        	private final javafx.scene.text.Text text = new javafx.scene.text.Text(); 
        	{
        		text.wrappingWidthProperty().bind(colBody.widthProperty().subtract(16)); //Turn on Text Wrapping
        		setGraphic(text); //Set the cell graphic to text
        		setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
        		setPrefHeight(Region.USE_COMPUTED_SIZE); //Compute the size required for the reply
        	}
        	
        	//When needed cell is updated
        	@Override
        	protected void updateItem(String item, boolean empty) {
        		super.updateItem(item, empty); //Normal Cell Function
        		
        		//If the item is empty or the string is empty set text to null
        		if (empty || item == null) {
        			text.setText(null);
        			setGraphic(null);
        			
        		//Else update the graphic normally
        		} else {
        			text.setText(item);
        			setGraphic(text);
        		}
        	}
        });


        //Add all the replies to the table
        repliesTable.getColumns().addAll(colDate, colName, colBody);
        repliesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); //This just makes it look better, it will fit the last col
        repliesTable.setPrefHeight(150);
        repliesTable.setPlaceholder(new Label("Be the first to reply?"));
        
        
        // Bottom: actions
        Button btnReply   = new Button("Reply");
        Button btnEdit = new Button("Edit");
        Button btnDelete  = new Button("Delete");
        Button btnClose       = new Button("Close");
        Button btnMarkResolved = (activePost.getSolved()) ? new Button("Re-Open Discussion") : new Button("Mark Resolved");
        
        //Bind all relevant actions
        btnClose.setOnAction(e -> viewStage.close());
        btnReply.setOnAction(e -> onCreateReply());
        btnEdit.setOnAction(e -> onEdit2());
        btnDelete.setOnAction(e -> onDelete2());
        btnMarkResolved.setOnAction(e -> onSolve());

        
        Region spacer = new Region(); //New Spacer
        HBox actions = new HBox(); //New HBox
        
        
        //This whole block basically determines what buttons are available based on the solved status, and the users permissions over the post
        if (!controller.canEdit(activePost, false, true) && !activePost.getSolved()) actions = new HBox(10, btnReply, btnEdit ,btnDelete, spacer, btnClose);
        else if (!controller.canEdit(activePost, false, true) && activePost.getSolved()) actions = new HBox(10, btnDelete, spacer, btnClose);
        else if (controller.canEdit(activePost, false, true) && activePost.getSolved()) actions = new HBox(10, btnMarkResolved, btnDelete, spacer, btnClose);
        else if (controller.canEdit(activePost, false, true) && !activePost.getSolved()) actions = new HBox(10, btnReply, btnMarkResolved, btnEdit ,btnDelete, spacer, btnClose);
        
        //Set Padding things
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.setPadding(new Insets(10));
        
        //New Label for a title for the replies section
        Label replies_Label = new Label("Replies");
        replies_Label.setFont(Font.font("Arial", 15));
        
        //Create the box for the bottom
        VBox bottomBox = new VBox(8, replies_Label,repliesTable, actions);
        bottomBox.setPadding(new Insets(10));
        VBox.setVgrow(repliesTable, Priority.NEVER);

        //Set the window up
        BorderPane root = new BorderPane();
        root.setTop(label_Header);
        BorderPane.setMargin(label_Header, new Insets(10, 10, 10, 10));
        root.setCenter(post_Body);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        viewStage.setScene(scene);

        refreshReplies(); //Refresh Replies
        

        viewStage.showAndWait();
    }
    
    
    /**
     * Clears the replies table then checks the HashMap of replies to put whatever relevant replies belong to the post
     */
    private void refreshReplies() {
    	repliesTable.getItems().clear();
    	List<Reply> replies = controller.getPostReplies(activePost.getID());
    	if (replies == null) return;
        ObservableList<Reply> data = FXCollections.observableArrayList(replies);
        repliesTable.setItems(data);
        repliesTable.refresh();
    }
    
    
    /**
     * Checks if a post is selected then opens up the post view window
     */
    private void viewPost() 
    {
    	Post postToView = selectedOrWarnPost();
    	
    	if(postToView != null)
    		showPost(postToView);
    }
    
    /**
     * Determines if their is a post selected or not.
     * If Not the user is shown a warning indicating such
     * 
     * @return the selected Post, or null if none are selected
     */
    private Post selectedOrWarnPost() {
        Post u = postsTable.getSelectionModel().getSelectedItem();
        if (u == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a Discussion Post").showAndWait(); //If no post selected show a warning
        }
        return u;
    }
    
    
    /**
     * Returns the selected reply (No warning for added functionality when no reply is selected within the view)
     * 
     * @return the Reply object selected from the table
     */
    private Reply selectedReply() {
        Reply u = repliesTable.getSelectionModel().getSelectedItem();
        return u;
    }
    
    
    
    /**
     * Refreshes the list of posts based on the filter values.
     * Creates and displays a new filtered list of values based on the selected filters 
     */
    private void refresh() {
        
    	//Load Latest summaries list
        List<Post> summaries = controller.listPosts();

        TextField search = (TextField)  filters.getChildren().get(1); //Get the search field
        @SuppressWarnings("rawtypes")
        ComboBox solvedFilter = (ComboBox) filters.getChildren().get(3); //Get the combo box

        String q = (search.getText() == null ? "" : search.getText().trim().toLowerCase()); //If there is nothing then the filter is "" otherwise trim the text and force it to lower case
        String status = (String) solvedFilter.getValue(); // "All", "Solved", "Unsolved"

        //If the table is set up already
        if (postsTable.getItems() instanceof FilteredList) 
        {
            FilteredList<Post> filtered = (FilteredList<Post>) postsTable.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<Post> allPosts = (ObservableList<Post>) filtered.getSource();
            allPosts.setAll(summaries); //Set the new list of items
            filtered.setPredicate(p -> {
            	
            	//Return false if p is null
            	if (p == null) return false;

                //If the status matches
                boolean statusMatch = "All".equals(status) || ("Solved".equals(status)   && p.getSolved()) || ("Unsolved".equals(status) && !p.getSolved());

                //If the body of the post matches
                boolean bodyMatch = q.isEmpty() || (p.getBody() != null && p.getBody().toLowerCase().contains(q));

                //Return the result of the matches
                return statusMatch && bodyMatch;
            });
            
        //If this is the first call of refresh
        } else {
        	
            ObservableList<Post> allPosts = FXCollections.observableArrayList(summaries); //Create new list using list of posts
            FilteredList<Post> filtered = new FilteredList<>(allPosts, p -> true); //Create a new filted list using the 
            postsTable.setItems(filtered); //Set the table to the filtered list

            
            //Set the predicate for the filtered list
            filtered.setPredicate(p -> {
                
            	//Return false if p is null
            	if (p == null) return false;

                //If the status matches
                boolean statusMatch = "All".equals(status) || ("Solved".equals(status)   && p.getSolved()) || ("Unsolved".equals(status) && !p.getSolved());

                //If the body of the post matches
                boolean bodyMatch = q.isEmpty() || (p.getBody() != null && p.getBody().toLowerCase().contains(q));

                //Return the result of the matches
                return statusMatch && bodyMatch;
            });
        }
    }
    

    /**
     * Operations to be completed when the create button is pressed
     */
    private void onCreate() 
    {
    	controller.createPost(); //Create a new post
    	refresh(); //Refresh table
    }
    
    
    /**
     * Operations to be completed when editing from the discussion posts page
     */
    private void onEdit1() 
    {
    	Post postToView = selectedOrWarnPost(); //Get selected post
    	
    	//If a post is selected
    	if(postToView != null) 
    		{
    			//If the user has permission to edit the post
    			if(controller.canEdit(postToView, false, false)) 
    			{
    				controller.editPost(postToView); //Edit post
    				refresh(); //Refresh
    			}
    		}
    }
    
    /**
     * Operations to be completed when deleting from the discussion posts page
     */
    private void onDelete1() 
    {
    	Post postToView = selectedOrWarnPost(); //Get selected post
    	
    	//If Post is selected
    	if(postToView != null) 
    		{
    			//If the user has permissions to delete
    			if(controller.canEdit(postToView, true, false)) 
    			{
    				controller.deletePost(postToView); //Delete Post
    				refresh(); //Refresh
    			}
    		}
    }
    
    /**
     * Operations to be completed when post is marked as resolved
     */
    private void onSolve() 
    {
    	boolean status = activePost.getSolved(); //Get last status
    	
    	//If it was already marked as resolved
    	if (status) 
    	{
    		controller.toggleResolved(activePost); //Toggle Status
    		refresh(); //Refresh
    		viewStage.close(); //Close the Window
    		showPost(activePost); //Re-open the window so now you have full control
    		
    	//If it is being solved for the first time
    	} else 
    	{
    		controller.toggleResolved(activePost); //Toggle Status
    		refresh(); //Refresh
    		viewStage.close(); //Close the view window
    	}
    }
    
    
    /**
     * Operations to be completed when editing a post/reply from the post view
     */
    private void onEdit2() 
    {
    	Reply replyToEdit = selectedReply(); //Get the selected reply
    	
    	
    	//If a reply is selected
    	if(replyToEdit != null) 
    		{
    			//If user has permissions to edit
    			if(controller.canEdit(replyToEdit, false, false)) 
    			{
    				controller.editReply(replyToEdit); //Edit Reply
    				refreshReplies(); //Refresh list
    			}
    		}
    	
    	//If no reply is selected attempt to edit the post
    	else 
    	{
    		//If user has permissions to edit
    		if(controller.canEdit(activePost, false, false)) 
    		{
    			//If the user edits the post
    			if(controller.editPost(activePost)) 
    			{
    				//Refresh the page
    				label_Header.setText(activePost.getHeader());
    				post_Body.setText(activePost.getBody());
    				viewStage.setTitle(activePost.getHeader() + " - " + activePost.getCreateInfoFormatted());
    				refresh();
    				
    			}
    			
    		}
    	}
    } 
    
    /**
     * Operations to be completed when deleting a post/reply from the post view
     */
    private void onDelete2() 
    {
    	Reply replyToDelete = selectedReply(); //Check for a selected reply
    	
    	
    	//If reply selected
    	if(replyToDelete != null) 
    		{
    			//Check Permission
    			if(controller.canEdit(replyToDelete, true, false)) 
    			{
    				//Delete Reply and Refresh
    				controller.deleteReply(replyToDelete);
    				refreshReplies();
    			}
    		}
    	
    	//If no reply selected attempt to delete the post
    	else 
    	{
    		//Check Permission
    		if(controller.canEdit(activePost, true, false)) 
    		{
    			//If post was deleted
    			if(controller.deletePost(activePost)) 
    			{
    				//Close window and refresh
    				viewStage.close();
    				refresh();
    			}
    			
    		}
    	}
    }
    
    /**
     * Operations to be completed when creating a reply from the post view
     */
    private void onCreateReply() 
    {
    	controller.createReply(activePost.getID());
    	refreshReplies();
    }
    
    
    
    /**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
    
}
