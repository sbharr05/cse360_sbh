package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import fieldCheckTools.passChecker;
import fieldCheckTools.userChecker;
import javafx.stage.Stage;

public class ControllerFirstAdmin {
	/*-********************************************************************************************
	
	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	private static String[] errorMessages = {"",""};

	/*-********************************************************************************************
	
	The User Interface Actions for this page
	
	*/

	/**********
	 * <p>
	 * Method: setAdminUsername()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user adds text to the username
	 * field in the View. A private local copy of what was last entered is kept
	 * here.
	 * </p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
		
		String errMsg = userChecker.checkForValidUserName(adminUsername);
		
		errorMessages[0] = (errMsg != "") ? "Username:\n"+ errMsg : "";
				
		String spacing = (errorMessages[0] != "") ? "\n" : "";
		
		ViewFirstAdmin.label_Invalid_Input.setText(errorMessages[0] + spacing + errorMessages[1]);
	}

	/**********
	 * <p>
	 * Method: setAdminPassword1()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user adds text to the password 1
	 * field in the View. A private local copy of what was last entered is kept
	 * here.
	 * </p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		
		//Check the input
		String errorMessage = passChecker.evaluatePassword(adminPassword1);
		
		errorMessages[1] = "";
		
		//If they do not match add to the helper message
		if(!adminPassword1.equals(adminPassword2)) 
		{
			//If it already has a password error replace it and keep user name error message
			errorMessages[1] = "Password:\nPasswords Do Not Match";
		} else if (errorMessage != "") 
		{
			errorMessages[1] = "Password:\n"+ errorMessage;
		}
		
		String spacing = (errorMessages[0] != "") ? "\n" : "";
		
		ViewFirstAdmin.label_Invalid_Input.setText(errorMessages[0] + spacing + errorMessages[1]);
	}

	/**********
	 * <p>
	 * Method: setAdminPassword2()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user adds text to the password 2
	 * field in the View. A private local copy of what was last entered is kept
	 * here.
	 * </p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword1.getText();
		
		//Check the input
		String errorMessage = passChecker.evaluatePassword(adminPassword2);
		
		errorMessages[1] = "";
		
		//If they do not match add to the helper message
		if(!adminPassword1.equals(adminPassword2)) 
		{
			//If it already has a password error replace it and keep user name error message
			errorMessages[1] = "Invalid Password:\nPasswords Do Not Match";
		} else if (errorMessage != "") 
		{
			errorMessages[1] = "Password:\n" + errorMessage;
		}
		
		String spacing = (errorMessages[0] != "") ? "\n" : "";
		
		ViewFirstAdmin.label_Invalid_Input.setText(errorMessages[0] + spacing + errorMessages[1]);
	}

	/**********
	 * <p>
	 * Method: doSetupAdmin()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user presses the button to set up
	 * the Admin account. It start by trying to establish a new user and placing
	 * that user into the database. If that is successful, we proceed to the
	 * UserUpdate page.
	 * </p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		String errMsg = "";
		String temp = "";
		//Check to make sure that the user name is valid
		//Set the errMsg string to the result of the user name check
		if ((errMsg = userChecker.checkForValidUserName(adminUsername)).compareTo("") != 0) 
		{
			errMsg = "Invalid Username:\n" + errMsg + "\n";
		}
		if (adminPassword1.compareTo(adminPassword2) != 0) 
		{
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			errMsg = errMsg + "Invalid Password:\nThe two passwords must match. Please try again!";
		//Using a temp var in order to make sure the error message is ordered properly and I don't have to run the password check again
		//Checks the password using the password checker then adds it to a temp variable
		} else if ((temp = passChecker.evaluatePassword(adminPassword1)).compareTo("") != 0) 
		{
			errMsg = errMsg + "Invalid Password:\n" + temp;
		}
		System.out.print(errMsg);
		//Make sure there are no error message to display 
		if (errMsg == "") 
		{
			// Create the passwords and proceed to the user home page
			User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, false);
			try {
				// Create a new User object with admin role and register in the database
				theDatabase.register(user);
			} catch (SQLException e) {
				System.err.println("*** ERROR *** Database error trying to register a user: " + e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
			
			// User was established in the database, so navigate to the User Update Page
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
			
		//If the error message contains an error set the errMsg to the do not match label.
		} else {
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("Operation Incomplete");
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText(errMsg);
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
		}
	}

	/**********
	 * <p>
	 * Method: performQuit()
	 * </p>
	 * 
	 * <p>
	 * Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted.
	 * </p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}
}
