package fieldCheckTools;

/*******
 * <p>
 * Title: passChecker Class
 * </p>
 * 
 * <p>
 * Description: This class is part of the field checker package and allows for consistent
 * checking of password fields throughout the application. When calling the main function it will
 * return an error message if the password is invalid.
 * </p>
 * 
 * 
 * @author Lucas Conklin, Lynn Robert Carter
 * 
 * 
 * @version 1.00 Function adapted from example given by Professor Carter to fit it's use case in the foundations code
 * 
 */
public class passChecker {
	
	
	public static String passwordErrorMessage = ""; // The error message text
	public static String passwordInput = ""; // The input being processed
	public static int passwordIndexofError = -1; // The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundLongEnough = false;
	private static String inputLine = ""; // The input line
	private static char currentChar; // The current character in the line
	private static int currentCharNdx; // The index of the current character
	private static boolean running;
	
	public static String evaluatePassword(String input) {
		// The following are the local variable used to perform the Directed Graph
		// simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0; // Initialize the IndexofError
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character

		if (input.length() <= 0) {
			return "The password is empty!";
		}

		// The input is not empty, so we can access the first character
		currentChar = input.charAt(0); // The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached
		// or at some
		// state the current character does not match any valid transition to a next
		// state. This
		// local variable is a working copy of the input.
		passwordInput = input; // Save a copy of the input

		// The following are the attributes associated with each of the requirements
		foundUpperCase = false; // Reset the Boolean flag
		foundLowerCase = false; // Reset the Boolean flag
		foundNumericDigit = false; // Reset the Boolean flag
		foundNumericDigit = false; // Reset the Boolean flag
		foundLongEnough = false; // Reset the Boolean flag

		// This flag determines whether the directed graph (FSM) loop is operating or
		// not
		running = true; // Start the loop

		// The Directed Graph simulation continues until the end of the input is reached
		// or at some
		// state the current character does not match any valid transition
		while (running) {
			// The cascading if statement sequentially tries the current character against
			// all of
			// the valid transitions, each associated with one of the requirements
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if("!#$%&'*+-/=?^_`{|}~.@".contains(currentChar + "")) {
				System.out.println("Special Char Found");
			} else {
				passwordIndexofError = currentCharNdx;
				return "An invalid character has been found!";
			}

			// Checks to see if the current char index goes over the maximum amount of
			// password characters (new)
			// Otherwise gives the standard length check to see if its at least 8 chars
			if (currentCharNdx > 32) {
				System.out.println("Password must be 32 characters or less");
				foundLongEnough = false;
			} else if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}

			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);

			System.out.println();
		}

		// Construct a String with a list of the requirement elements that were found.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case, ";

		if (!foundLowerCase)
			errMessage += "Lower case, ";

		if (!foundNumericDigit)
			errMessage += "Numeric digit, ";

		if (!foundLongEnough)
			errMessage += "Between 8-32 Chars, "; // Changed the text of this to be more in line with the new length

		if (errMessage == "")
			return "";

		// If it gets here, there something was not found, so return an appropriate
		// message
		passwordIndexofError = currentCharNdx;
		
		//The setup of this may seem strange, but I am essentially always cutting off the last part of the string to add a period instead
		return "Your password needs " + errMessage.substring(0, errMessage.length() - 2) + ".";
	}
}
