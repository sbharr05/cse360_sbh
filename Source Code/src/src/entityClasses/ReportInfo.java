package entityClasses;



/*******
 * <p>
 * Title: ReportInfo<T>
 * </p>
 * 
 * <p>
 * Description: This class is a container for the info needed when viewing and interacting with user generated content reports.
 * It uses a generic type so that it can be constructed both post and reply objects. It contains the text content as well as an incremented variable to track
 * the number of the reports
 * </p>
 * 
 * 
 * @author Sutton Harr 
 * 
 * @version 1.03 
 * @see Post
 * @See Reply
 * 
 * 
 */
public class ReportInfo {
	private int reportsNum = 0;
	private DiscussionBoardContent object;
	private String id;
	
	
	/**
	 * Constructor for Report info from save
	 * 
	 * @param obj content class for the report
	 * @param reports number of reports
	 * @param ID the unique ID of the offending content
	 */
	public ReportInfo(DiscussionBoardContent obj, int reports, String ID)
	{
		object = obj;
		reportsNum = reports;
		id = ID;
	}
	
	/**
	 * Constructor for new Report Info
	 * 
	 * @param obj content class for the report
	 * @param ID the unique ID of the offending content
	 */
	public ReportInfo(DiscussionBoardContent obj, String ID) 
	{
		reportsNum = 1;
		id = ID;
		object = obj;
	}
	
	/**
	 * Getter Method for the report content
	 * 
	 * @return content associated with report
	 */
	public DiscussionBoardContent getObject() 
	{
		return object;
	}
	
	/**
	 * Method to increment the reports on a post
	 */
	public void report() 
	{
		reportsNum++;
	}
	
	/**
	 * Getter Method for the number of reports
	 * 
	 * @return total reports of this content
	 */
	public int getNumber() 
	{
		return reportsNum;
	}
	
	/**
	 * Getter Method for the related content id
	 * 
	 * @return the id of the related content
	 */
	public String getID() 
	{
		return id;
	}
}
