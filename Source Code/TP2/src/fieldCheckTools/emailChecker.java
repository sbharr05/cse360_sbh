package fieldCheckTools;

/*******
 * <p>
 * Title: emailChecker Class
 * </p>
 * 
 * <p>
 * Description: This class is part of the field checker package and allows for consistent
 * checking of email fields throughout the application. When calling the main function it will
 * return an error message if the password is invalid.
 * </p>
 * 
 * 
 * @author Lucas Conklin, 
 * 
 * 
 * 
 */
public class emailChecker 
{
	public static String emailErrorMessage = "";  //Error Message to be returned
	public static String emailInput = "";  //Provided input
	
	//Checker to determine if there is a given char is a special char
	public static boolean isSpecialChar(char c) {
	    String specials = "!#$%&'*+-/=?^_`{|}~."; //String of all special characters
	    return specials.indexOf(c) >= 0; //Compare index of
	}
	
	//Helper function to check if a string contains anything other than alphanumeric characters
	public static boolean containsNonAlphaNumeric(String Input) 
	{
		char currChar;
		
		for(int i = 0; i < Input.length(); i++) 
		{
			currChar = Input.charAt(i);
			
			if (!((currChar >= 'A' && currChar <= 'Z') || // Check for A-Z
				(currChar >= 'a' && currChar <= 'z') ||   // Check for a-z
				(currChar >= '0' && currChar <= '9')))    //Check for numbers
				return true; //Non Alphanumeric character found
		}
		
		return false; //No non alphanumeric characters found
	}
	
	//This function returns a string with the error message given an email input, return nothing if the input is valid
	public static String evaluateEmail(String emailIn) 
	{
		emailInput = emailIn;
		
		//Determine the local part of the email, if there is no @ symbol email automatically is invalid
		int localIndexEnd = emailInput.indexOf('@');
		
		//If no @ or . symbol or there is any spaces located in the input return error
		if(localIndexEnd == -1 || emailInput.indexOf('.') == -1 || emailInput.indexOf(' ') != -1) 
		{
			return "Invalid Email:\nMust be in the form example@email.com";
		}
		
		
		//Split the email into its two parts, local and domain
		String localIn = emailInput.substring(0, localIndexEnd);  //Obtain Local Substring
		String domainIn = emailInput.substring(localIndexEnd + 1); //Obtain Domain Substring
		
		//For the local part of the email there are very few restrictions, no periods twice in a row, no special characters at the beginning or end, and 1 <= Size < 64
		//This means it can be validated very easily, there are also restrictions on @ symbols but this can be resolved later
		if (localIn.indexOf("..") != -1 || isSpecialChar(localIn.charAt(0)) || isSpecialChar(localIn.charAt(localIn.length() - 1)) || localIn.length() < 1 || localIn.length() > 64) //Check for above conditions
		{
			return "Invalid Email:\nMust be in the form example@email.com"; //Return Invalid
		}
		
		
		//Next we need to check the domain part
		//No repeated periods, greater than 4 chars and less than 256, and must at least have 1 period
		if(domainIn.indexOf("..") != -1 || domainIn.length() < 4 || domainIn.length() > 255 || domainIn.indexOf('.') == -1) 
		{
			return "Invalid Email:\nMust be in the form example@email.com";
		}
		
		//Now Special Chars Need to be checked
		
		//Find first path
		String[] domainPaths = domainIn.split("\\."); //Split the domain by each .
		
		
		//For each section in the domain check if it has special characters and meets length requirements
		for(int i = 0; i < domainPaths.length; i++) 
		{
			//Check for length requirements and special chars
			if (containsNonAlphaNumeric(domainPaths[i]) || domainPaths[i].length() > 63 || domainPaths[i].length() < 1) 
			{
				return "Invalid Email:\nMust be in the form example@email.com";
			}
			
		}
		
			
			
		return "";
	}
	
}