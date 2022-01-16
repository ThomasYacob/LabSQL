package com.redvethomas.labsql.View;

import com.redvethomas.labsql.Model.User;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;


/**
 * A simplified example of a form, using JavaFX Dialog and DialogPane. Type
 * parameterized for Book.
 *
 * @authors Thomas Yacob & Redve Ahmed
 */

public class UserDialog extends Dialog<User>{

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    public UserDialog(){
        buildAddUserDialog();
    }

    private void buildAddUserDialog(){

        this.setTitle("User");
        this.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Username "), 1, 1);
        grid.add(usernameField, 2, 1);
        grid.add(new Label("Password"), 1, 2);
        grid.add(passwordField, 2, 2);

        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk
                = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel
                = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        ButtonType guestField = new ButtonType("Guest", ButtonBar.ButtonData.APPLY);
        this.getDialogPane().getButtonTypes().add(guestField);
        // this callback returns the result from our dialog, via
        // Optional<FooBook> result = dialog.showAndWait();
        // FooBook user = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, User>() {
            @Override
            public User call(ButtonType b) {
                User result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new User(
                                usernameField.getText(),
                                passwordField.getText());
                    }
                    else if(b == guestField) {
                        result = new User("Guest", "Password1!");

                    }
                }

                clearFormData();
                return result;
            }
        });
        // add an event filter to keep the dialog active if validation fails
        // (yes, this is ugly in FX)
        Button okButton
                = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!isValidData()) {
                    event.consume();
                    showErrorAlert("Form error", "Invalid input");
                }
            }
        });

    }

    private boolean isValidData() {
        if (usernameField.getText() == null) {
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            return false;
        }
        // if(...) - keep on validating user input...

        return true;
    }

    private void clearFormData() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
