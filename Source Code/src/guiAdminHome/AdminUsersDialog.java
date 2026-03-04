package guiAdminHome;

import admin.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.List;
import java.util.Optional;

public class AdminUsersDialog {
	
	
    private final AdminService service = new AdminServiceImpl();
    private final TableView<UserSummary> table = new TableView<>();
    private final ComboBox<Role> roleFilter = new ComboBox<>();
    private final CheckBox activeOnly = new CheckBox("Active only");

    public static void display(Stage owner) {
        new AdminUsersDialog().show(owner);
    }

    @SuppressWarnings("unchecked")
	private void show(Stage owner) {
        Stage dlg = new Stage();
        dlg.initOwner(owner);
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.setTitle("Admin — Users");

        // Top: filters
        roleFilter.getItems().add(null); // "no role filter"
        roleFilter.getItems().addAll(Role.values());
        roleFilter.setPromptText("Role filter (optional)");
        HBox filters = new HBox(10, new Label("Role:"), roleFilter, activeOnly, makeRefreshButton());
        filters.setPadding(new Insets(10));

        // Center: table

     // User ID
     TableColumn<UserSummary, String> colId = new TableColumn<>("User ID");
     colId.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUserId()));
     colId.setPrefWidth(220);

     // Display Name
     TableColumn<UserSummary, String> colName = new TableColumn<>("Display Name");
     colName.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDisplayName()));
     colName.setPrefWidth(180);

     // Role (keep as lambda)
     TableColumn<UserSummary, String> colRole = new TableColumn<>("Role");
     colRole.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getRole().name()));
     colRole.setPrefWidth(100);

     // Active (keep as lambda)
     TableColumn<UserSummary, String> colActive = new TableColumn<>("Active");
     colActive.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().isActive() ? "Yes" : "No"));
     colActive.setPrefWidth(80);


        table.getColumns().addAll(colId, colName, colRole, colActive);
        table.setPrefHeight(360);

        // Bottom: actions
        Button btnActivate    = new Button("Activate");
        Button btnDeactivate  = new Button("Deactivate");
        Button btnChangeRole  = new Button("Change Role");
        Button btnResetPass   = new Button("Reset Password");
        Button btnClose       = new Button("Close");

        btnActivate.setOnAction(e -> onActivate(true));
        btnDeactivate.setOnAction(e -> onActivate(false));
        btnChangeRole.setOnAction(e -> onChangeRole());
        btnResetPass.setOnAction(e -> onResetPassword());
        btnClose.setOnAction(e -> dlg.close());

        HBox actions = new HBox(10, btnActivate, btnDeactivate, btnChangeRole, btnResetPass, new Region(), btnClose);
        HBox.setHgrow(actions.getChildren().get(actions.getChildren().size()-2), Priority.ALWAYS);
        actions.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(filters);
        root.setCenter(new VBox(5, table));
        root.setBottom(actions);

        Scene scene = new Scene(root, 720, 520);
        dlg.setScene(scene);

        // Initial load
        refresh();

        dlg.showAndWait();
    }

    // ADM-01: List users (with optional filters)
    private Button makeRefreshButton() {
        Button b = new Button("Refresh");
        b.setOnAction(e -> refresh());
        return b;
    }

    private void refresh() {
        Optional<Role> rf = Optional.ofNullable(roleFilter.getValue());
        Optional<Boolean> af = activeOnly.isSelected() ? Optional.of(Boolean.TRUE) : Optional.empty();
        List<UserSummary> users = service.listUsers(rf, af);
        ObservableList<UserSummary> data = FXCollections.observableArrayList(users);
        table.setItems(data);
    }

    private UserSummary selectedOrWarn() {
        UserSummary u = table.getSelectionModel().getSelectedItem();
        if (u == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a user in the table.").showAndWait();
        }
        return u;
    }

    // ADM-02: Activate/Deactivate
    private void onActivate(boolean active) {
        UserSummary u = selectedOrWarn();
        if (u == null) return;
        boolean ok = service.setActive(u.getUserId(), active);
        Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                (ok ? (active ? "Activated: " : "Deactivated: ") : "Operation failed: ") + u.getUserId());
        alert.setTitle(ok ? "Operation Success" : "Operation Incomplete");
        alert.setHeaderText("");
        alert.showAndWait();
        
        if (ok) refresh();
    }

    // ADM-03: Change role
    private void onChangeRole() {
        UserSummary u = selectedOrWarn();
        if (u == null) return;
        ChoiceDialog<Role> dlg = new ChoiceDialog<>(u.getRole(), Role.values());
        dlg.setTitle("Change Role");
        dlg.setHeaderText("Change role for: " + u.getUserId());
        dlg.setContentText("Select new role:");
        dlg.showAndWait().ifPresent(newRole -> {
        	
        	//If the Role is none it will have to deactivate the user
        	if(newRole == Role.NONE) 
        	{
        		//Give an Alert Letting the User know it will deactivate the account
        		Alert noneConfirm = new Alert(Alert.AlertType.WARNING,"Setting a users role to NONE will deactivate their account. Proceed?", ButtonType.YES, ButtonType.NO);
        		noneConfirm.setHeaderText("Warning!");
        		ButtonType confirmAnswer = noneConfirm.showAndWait().orElse(ButtonType.NO);
        		
        		//If they answer yes attempt to deactivate the account
        		if (confirmAnswer == ButtonType.YES) 
    			{
        			boolean ok = service.setActive(u.getUserId(), false); //Ok Bool Determines if the operation was a success
        			
        			
        			// If not ok show an Alert and cancel role change operation
        			if (!ok) 
        			{
        				Alert alert = new Alert(Alert.AlertType.ERROR, "Reason: Protect Last Admin");
        				alert.setTitle("Operation Incomplete");
        				alert.setHeaderText("This Action Cannot Be Done");
        				alert.showAndWait();
        				return;
        			}
        			
    			} else return; //If User Elected to not change the role to none cancel the role change operation
        	}
        	
            boolean ok = service.changeRole(u.getUserId(), newRole);
            Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    ok ? ("Role updated to " + newRole + " for " + u.getUserId())
                       : "Reason: Protect Last Admin");
            alert.setTitle(ok ? "Operation Success" : "Operation Incomplete");
            alert.setHeaderText(ok ? "Role Successfully Changed": "This Action Cannot Be Done");
            alert.showAndWait();
            if (ok) refresh();
        });
    }

    // ADM-04: Reset password
    private void onResetPassword() {
        UserSummary u = selectedOrWarn();
        if (u == null) return;
        String temp = service.resetPassword(u.getUserId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Temporary password for " + u.getUserId() + ":\n\n" + temp +
                "\n\nUser will be forced to change it at next login.");
        alert.setTitle("Password Reset");
        alert.setHeaderText("");
        alert.showAndWait();
        
    }
}
