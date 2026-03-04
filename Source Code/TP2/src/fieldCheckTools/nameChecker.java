package fieldCheckTools;

/*******
 * <p>
 * Title: nameChecker Class
 * </p>
 * 
 * <p>
 * Description: This class is part of the field checker package and allows for consistent
 * checking of name fields throughout the application. When calling the main function it will
 * return an error message if the password is invalid.
 * </p>
 * 
 * 
 * @author Lucas Conklin, 
 * 
 * 
 * 
 */

public class nameChecker {
	
	
	//Adapted function from the email checker class, returns true if the input contains a non alphabetical character
	public static boolean containsNonAlpha(String Input) 
	{
		char currChar;
		
		for(int i = 0; i < Input.length(); i++) 
		{
			currChar = Input.charAt(i);
			
			if (!((currChar >= 'A' && currChar <= 'Z') || // Check for A-Z
				(currChar >= 'a' && currChar <= 'z')))    // Check for a-z
				return true; //Non Alphanumeric character found
		}
		
		return false; //No non alphanumeric characters found
	}
	
	//Checks if input is valid
	public static String evaluateName(String name) 
	{
		String newName = name;
		
		if (newName.length() < 1)         //Check if Input Too Short 
			return "Input Empty";
		else if (newName.length() > 255)  //Check if Input Too Long
			return "Input Too Long";
		
		
		//Check if the name has double dashes, or has one at beginning/end
		if(newName.contains("--") || newName.charAt(0) == '-' || newName.charAt(newName.length() - 1) == '-') 
		{
			return "Invalid Use of Dashes"; //Return Error Message
		}
		
		newName = name.replaceAll("-", ""); //Remove all dashes
		
		//Check if non alphabetical characters are used
		if(containsNonAlpha(newName)) 
			return "Contains Non-Alphabetical Characters"; //Return Error Message
		else
			return "";
	}
	
	
	//Formats a Name so the capitals are correct
	public static String formatName(String input) 
	{
		String[] sections = input.split("-");
		
		//If No Dashes Return Capitalized First Letter
		if(sections.length < 2) return input.substring(0, 1).toUpperCase() + input.substring(1);
		
		String out = "";
		
		//Capitalize all names between dashes and return
		for(int i = 0; i < sections.length; i++) 
		{
			out += sections[i].substring(0, 1).toUpperCase() + sections[i].substring(1);
			if (i != sections.length - 1) out += "-";
		}
		
		return out;
	}
}
