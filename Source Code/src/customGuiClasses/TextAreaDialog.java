package customGuiClasses;

import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

public class TextAreaDialog {
		//Dialog box component
		private Dialog<String> dialog = new Dialog<>();
		
		//The 2 Text Field Components
		private TextArea tArea = new TextArea();
		
		//Constructor which sets up the text components of the dialog container and text area
		public TextAreaDialog(String title, String header, String pHolder)
		{
			SetupDialog();
			dialog.setTitle(title);
			dialog.setHeaderText(header);
			tArea.setPromptText(pHolder);
		}
		
		//Basic Constructor Creates Empty Dialog Box
		public TextAreaDialog() {SetupDialog();}
		
		
		//Constructs the 2 Input Dialog GUI
		public void SetupDialog() 
		{
			DialogPane dialogPane = dialog.getDialogPane();
			
			dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			dialogPane.setContent(new VBox(10, tArea));
			dialog.setResultConverter((ButtonType button) -> {
	            if (button == ButtonType.OK) {
	                return tArea.getText();
	            }
	            return null;
	        });
			
	        dialog.setOnShown(e -> tArea.requestFocus());
	        changeTextAreaSize(200, 20);
		}
		
		//This is somewhat of an override of the Dialog Show and Wait Function
		//Returns the TwoStringResults class optional that was set by the dialog box
		public Optional<String> showAndWait() 
		{
			dialog.showAndWait();
			
			Optional<String> toReturn = Optional.ofNullable(dialog.getResult());
			return toReturn;
		}
		
		
		//Getter Method that returns the dialog box
		public Dialog<String> getDialog() 
		{
			return dialog;
		}
		
		//Getter Method that returns the first text field
		public TextArea getTextArea() 
		{
			return tArea;
		}
		
		//Sets the content text of the dialog container
		public void setContentText(String content) 
		{
			dialog.setContentText(content);
		}
		
		//Clears the input fields
		public void clearInputFields() 
		{
			tArea.setText("");
		}
		
		public void changeWindowSize(double minWidth, double minHeight) {
		    dialog.setResizable(true);
		    if (minWidth  > 0) dialog.getDialogPane().setMinWidth(minWidth);
		    if (minHeight > 0) dialog.getDialogPane().setMinHeight(minHeight);
		}

		public void changeTextAreaSize(double w1, double h1) {
		    if (w1 > 0) tArea.setPrefWidth(w1);
		    if (h1 > 0) tArea.setPrefHeight(h1);
		    tArea.setMaxWidth(Double.MAX_VALUE);
		}
		
		// Limit the text Area
		public void setMaxChars( int max) {
		    tArea.setTextFormatter(new TextFormatter<String>(change -> {
		        return change.getControlNewText().length() <= max ? change : null;
		    }));
		}
}
