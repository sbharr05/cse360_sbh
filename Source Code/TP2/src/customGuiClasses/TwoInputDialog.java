package customGuiClasses;

import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/*******
 * <p>
 * Title: Two Input Dialog Class
 * </p>
 * 
 * <p>
 * Description: This class is a custom gui component that allows the use of 2 different input fields
 * it is functionally the same as the input dialog but returns a custom class that contains 2 strings
 * </p>
 * 
 * 
 * @author Sutton Harr
 * 
 * @version 1.1
 * 
 */
public class TwoInputDialog {

	
	//Dialog box component
	private Dialog<TwoStringResults> dialog = new Dialog<>();
	
	//The 2 Text Field Components
	private TextArea tField1 = new TextArea();
	private TextArea tField2 = new TextArea();
	
	//Constructor which sets up the text components of the dialog container and text fields
	public TwoInputDialog(String title, String header, String pHolder1, String pHolder2)
	{
		SetupDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		tField1.setPromptText(pHolder1);
		tField2.setPromptText(pHolder2);
	}
	
	//Basic Constructor Creates Empty Dialog Box
	public TwoInputDialog() {SetupDialog();}
	
	
	//Constructs the 2 Input Dialog GUI
	public void SetupDialog() 
	{
		DialogPane dialogPane = dialog.getDialogPane();
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(new VBox(10, tField1, tField2));
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new TwoStringResults(tField1.getText(), tField2.getText());
            }
            return null;
        });
		
        dialog.setOnShown(e -> tField1.requestFocus());
        changeTextFieldSizes(200, 20, 200, 20);
	}
	
	
	//Getter Method that returns the dialog box
	public Dialog<TwoStringResults> getDialog() 
	{
		return dialog;
	}
	
	//Getter Method that returns the first text field
	public TextArea getTextField1() 
	{
		return tField1;
	}
	
	//Getter Method that returns the second text field
	public TextArea getTextField2() 
	{
		return tField2;
	}
	
	//This is somewhat of an override of the Dialog Show and Wait Function
	//Returns the TwoStringResults class optional that was set by the dialog box
	public Optional<TwoStringResults> showAndWait() 
	{
		dialog.showAndWait();
		
		Optional<TwoStringResults> toReturn = Optional.ofNullable(dialog.getResult());
		return toReturn;
	}
	
	//Sets the content text of the dialog container
	public void setContentText(String content) 
	{
		dialog.setContentText(content);
	}
	
	//Clears the input fields
	public void clearInputFields() 
	{
		tField1.setText("");
		tField2.setText("");
	}
	
	public void changeWindowSize(double minWidth, double minHeight) {
	    dialog.setResizable(true);
	    if (minWidth  > 0) dialog.getDialogPane().setMinWidth(minWidth);
	    if (minHeight > 0) dialog.getDialogPane().setMinHeight(minHeight);
	}

	public void changeTextFieldSizes(double w1, double h1, double w2, double h2) {
	    if (w1 > 0) tField1.setPrefWidth(w1);
	    if (h1 > 0) tField1.setPrefHeight(h1);
	    if (w2 > 0) tField2.setPrefWidth(w2);
	    if (h2 > 0) tField2.setPrefHeight(h2);
	    tField1.setMaxWidth(Double.MAX_VALUE);
	    tField2.setMaxWidth(Double.MAX_VALUE);
	}
	
	//Set the max chars of a text area
	public static void setMaxChars(TextArea tf, int max) {
	    tf.setTextFormatter(new TextFormatter<String>(change -> {
	        return change.getControlNewText().length() <= max ? change : null;
	    }));
	}

	// For your two fields at once
	public void setMaxCharsForFields(int max1, int max2) {
	    setMaxChars(tField1, max1);
	    setMaxChars(tField2, max2);
	}
	
	//Custom Class to hold the output of the 2 text fields
	public static class TwoStringResults
	{
		String text1;
		String text2;
		
		public TwoStringResults(String t1, String t2) 
		{
			text1 = t1;
			text2 = t2;
		}
		
		public String getText1() {return text1;}
		public String getText2() {return text2;}
	}
}
