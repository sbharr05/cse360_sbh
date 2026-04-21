package fieldCheckTools;

/*******
 * <p>
 * Title: contentLanguageChecker
 * </p>
 * 
 * <p>
 * Description: This class contains a static method for parsing the language of content uploaded to the application
 * it will flag any content violations and subsequently return false. There are several different types of language configured to be checked
 * by the parser, each of which must be hard-coded into the arrays within the class.
 * </p>
 * 
 * 
 * @author Sutton Harr 
 * 
 * @version 1.03 
 * 
 */
public class contentLanguageChecker {

	//This is a list of words that are banned in all contexts 
	private static final String[] bannedWords = {"placeholder"};
	
	//Words which are banned but could be used as a root word for others
	//They must be checked first with spaces as context
	private static final String[] bannedButRoot = {"butt"};
	
	//This is a list of words which are banned when used with direction
	private static final String[] bannedInContext = {"stupid","hate", "kill", "attack", "hurt", "stinky", "bad"};
	
	//This is a list of words which could indicate targeted language
	private static final String[] directionIndicators = {"you", "you are", "is", "is a", "is an", "you're", "they are", "they're","she is","she's","he is","he's","him","her"};
	
	
	//Helper function to replace special characters with letters
	private static String replaceSpecial(String input) 
	{
			return input
		            .toLowerCase() //Set the input to lower case
					
		            //Replace characters which could be used to represent letters i.e pl@c3h0ld3r -> placeholder
		            .replace('@', 'a')
		            .replace('4', 'a')
		            .replace('0', 'o')
		            .replace('1', 'i')
		            .replace('3', 'e')
		            .replace('$', 's')
		            .replace('5', 's')
		            .replace('!', 'i')
		            .replace('7', 't')
		            
		            //Replace other special characters with spaces
					.replaceAll("[^a-z0-9]", " ");
	}
	
	//Helper function to remove all special characters and punctuation
	private static String normalizePunctuation(String input) 
	{
		return input
				
				//Delete all punctuation and spaces to normalize string
				.replaceAll("[^a-z0-9 ]", "")
				.replace(" ", "");
	}
	
	//Helper function which calls both other helper functions to normalize text
	private static String normalize(String input) {return (normalizePunctuation(replaceSpecial(input)));}
	
	//Helper function to determine if the provided index is one of the bounds values of the string
	private static boolean isWordBoundry (String str, int index) 
	{
		if(index < 0 || index >= str.length()) return true; //If out of bounds return true
		Character c = str.charAt(index);
		return !Character.isLetter(c); //If the char is not a letter return true
	}
	
	
	/**
	 * Method which parses input and determines if it contains language which is inappropriate or un-professional
	 * 
	 * @param inputRaw the raw input of the string to parse
	 * @return a boolean value based on if the input was flagged as inappropriate
	 */
	public static boolean checkContent(String inputRaw) 
	{
		String input = replaceSpecial(inputRaw); //Replace all special chars with letters
		
		//Iterate through the banned root words and check for matches
		for(int i = 0; i < bannedButRoot.length; i++) 
		{
			int index = input.indexOf(bannedButRoot[i]);
			
			if(isWordBoundry(input ,index - 1) & isWordBoundry(input,index + bannedButRoot[i].length())) return false; //If the banned root word is contained as it's own word return false
		}
		
		
		input = replaceSpecial(inputRaw); //Normalize the input
		
		//Check for any reference to listed banned words
		for(int i = 0; i < bannedWords.length ; i++) 
		{
			if(input.contains(bannedWords[i])) return false; //If the banned word is in the input return false
		}
		
		//Iterate through all the words banned in context
		for(int i = 0; i < bannedInContext.length; i++) 
		{
			
			//If the banned word is contained in the input move to next check
			if(input.contains(bannedInContext[i])) 
			{
				
				//Trim the string so that the substring is +- 10 chars from the index of the flag
				int index = input.indexOf(bannedInContext[i]); //Find index
				int start = Math.max(0, index - 10); //Find Start
				int end   = Math.min(input.length(), index + bannedInContext[i].length() + 10); //Find end
				
				//Trim String
				String sub = input.substring(start, end);
				
				//Iterate through list of direction indicators
				for(int j = 0; j < directionIndicators.length; j++) 
				{
					if(sub.contains(normalize(directionIndicators[j]))) return false; //If it contains banned word and direction indicator return false
				}
				
				
			}
		}
		
		return true;
	}
}
