package guiUserLogin;

import database.Database;
import entityClasses.User;
import fieldCheckTools.passChecker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.util.Optional;

import customGuiClasses.TwoInputDialog;
import customGuiClasses.TwoInputDialog.TwoStringResults;

public class ControllerUserLogin {

	/*-********************************************************************************************
	
	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;

	/**********
	 * <p>
	 * Method: public doLogin()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user has clicked on the Login
	 * button. This method checks the username and password to see if they are
	 * valid. If so, it then logs that user in my determining which role to use.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed
	 * rather than passing that information as parameters.
	 * 
	 */
	protected static void doLogin(Stage ts) {
		theStage = ts;
		String username = ViewUserLogin.text_Username.getText();
		String password = ViewUserLogin.text_Password.getText();
		boolean loginResult = false;

		ViewUserLogin.alertUsernamePasswordError.setTitle("Error");
		
		// Fetch the user and verify the username
		if (theDatabase.getUserAccountDetails(username) == false) {
			// Don't provide too much information. Don't say the username is invalid or the
			// password is invalid. Just say the pair is invalid.
			ViewUserLogin.alertUsernamePasswordError.setHeaderText("Incorrect username/password");
			ViewUserLogin.alertUsernamePasswordError.setContentText("Please Make Corrections and Try again!");
			ViewUserLogin.alertUsernamePasswordError.showAndWait();
			return;
		}
		System.out.println("*** Username is valid");

		// Check to see that the login password matches the account password
		String actualPassword = theDatabase.getCurrentPassword();

		if (password.compareTo(actualPassword) != 0) {
			ViewUserLogin.alertUsernamePasswordError.setHeaderText("Incorrect username/password");
			ViewUserLogin.alertUsernamePasswordError.setContentText("Please Make Corrections and Try again!");
			ViewUserLogin.alertUsernamePasswordError.showAndWait();
			return;
		}
		System.out.println("*** Password is valid for this user");
		
		boolean accountActive = theDatabase.getActiveStatus(username);
		
		//If the user's account has been deactivated stop login process
		if (!accountActive) 
		{
			ViewUserLogin.alertUsernamePasswordError.setHeaderText("This Account Has Been Disabled :(");
			ViewUserLogin.alertUsernamePasswordError.setContentText("Contact an administrator to regain access.");
			ViewUserLogin.alertUsernamePasswordError.showAndWait();
			return;
		}
		
		boolean mustResetPassword = theDatabase.doesUserNeedPasswordReset(username);
		
		//If the user needs to reset their password force them to reset the password
		if (mustResetPassword) 
		{
			//Create New Two Input Dialog
			TwoInputDialog passwordChange = new TwoInputDialog("Change Password", "Your password was reset.\nCreate a new one to continue.", "New Password", "Confirm New Password");
			
			//Create Alert for Invalid Passwords
			Alert invalidAlert = new Alert(AlertType.ERROR);
			invalidAlert.setTitle("Operation Incomplete");
			invalidAlert.setHeaderText("Invalid Password");
			
			//Create Var For Dialog Results
			Optional<TwoStringResults> pChangeResults;
			
			//Error Message String
			String errMessage = "placeholder";
			
			//Repeat Until Password Has Successfully Changed
			while(errMessage != "") 
			{
				pChangeResults = passwordChange.showAndWait(); //Show Dialog
				
				if(pChangeResults.isPresent()) 
				{
					TwoStringResults inputs = pChangeResults.get();
					
					//Get the output of the dialog
					String pInput1 = inputs.getText1();
					String pInput2 = inputs.getText2();
					
					
					
					//If the passwords do not match
					if (!pInput1.equals(pInput2)) 
					{
						errMessage = "Passwords Do Not Match";
					} else 
					{
						errMessage = passChecker.evaluatePassword(pInput1);   //Use Password Checker to validate new password
					}
					
					//If there is no error update the password
					if (errMessage == "") 
					{
						theDatabase.updatePassword(username, pInput1);
						theDatabase.updateUserPasswordReset(username, false);
						password = pInput1;
						invalidAlert.setAlertType(AlertType.INFORMATION);
						invalidAlert.setTitle("Operation Success");
						invalidAlert.setHeaderText("Password Has Been Changed");
						invalidAlert.setContentText("Please log in using new password.");
						invalidAlert.showAndWait();
					} else 
					{
						//Setup and show error dialog
						invalidAlert.setContentText("Reason: " + errMessage + "\n\nPlease Make Changes and Try Again");
						invalidAlert.showAndWait();
					}
				}
			}
		}

		// Establish this user's details
		User user = new User(username, password, theDatabase.getCurrentFirstName(), theDatabase.getCurrentMiddleName(),
				theDatabase.getCurrentLastName(), theDatabase.getCurrentPreferredFirstName(),
				theDatabase.getCurrentEmailAddress(), theDatabase.getCurrentAdminRole(),
				theDatabase.getCurrentNewRole1(), theDatabase.getCurrentNewRole2());

		// See which home page dispatch to use
		int numberOfRoles = theDatabase.getNumberOfRoles(user);
		System.out.println("*** The number of roles: " + numberOfRoles);
		if (numberOfRoles == 1) {
			// Single Account Home Page - The user has no choice here

			// Admin role
			if (user.getAdminRole()) {
				loginResult = theDatabase.loginAdmin(user);
				if (loginResult) {
					guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
				}
			} else if (user.getNewRole1()) {
				loginResult = theDatabase.loginRole1(user);
				if (loginResult) {
					guiRole1.ViewRole1Home.displayRole1Home(theStage, user);
				}
			} else if (user.getNewRole2()) {
				loginResult = theDatabase.loginRole2(user);
				if (loginResult) {
					guiRole2.ViewRole2Home.displayRole2Home(theStage, user);
				}
				// Other roles
			} else {
				System.out.println("***** UserLogin goToUserHome request has an invalid role");
			}
		} else if (numberOfRoles > 1) {
			// Multiple Account Home Page - The user chooses which role to play
			System.out.println("*** Going to displayMultipleRoleDispatch");
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.displayMultipleRoleDispatch(theStage, user);
		}
	}

	/**********
	 * <p>
	 * Method: setup()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called to reset the page and then populate it
	 * with new content.
	 * </p>
	 * 
	 */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
	}

	/**********
	 * <p>
	 * Method: public performQuit()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the user has clicked on the Quit
	 * button. Doing this terminates the execution of the application. All important
	 * data must be stored in the database, so there is no cleanup required. (This
	 * is important so we can minimize the impact of crashed.)
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}

}
