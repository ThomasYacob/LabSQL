package com.redvethomas.labsql.View;

import com.redvethomas.labsql.Model.Book;
import com.redvethomas.labsql.Model.Review;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import static java.sql.Date.valueOf;

/**
 * A simplified example of a form, using JavaFX Dialog and DialogPane. Type
 * parameterized for Book.
 *
 * @author Redve Ahmed & Thomas Yacob
 */
public class ReviewDialog extends Dialog<Review> {

    private final ComboBox<Integer> ratingField = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    private final TextField reviewField = new TextField();
    private final DatePicker reviewDateField = new DatePicker();

    public ReviewDialog() {
        buildReviewDialog();
    }

    private void buildReviewDialog() {

        this.setTitle("Add a new review");
        this.setResizable(false); // really?

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Rating"), 1, 1);
        grid.add(ratingField, 2, 1);
        grid.add(new Label("Review"), 1, 2);
        grid.add(reviewField, 2, 2);
        grid.add(new Label("Review Date"), 1, 3);
        grid.add(reviewDateField, 2, 3);

        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk
                = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel
                = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // this callback returns the result from our dialog, via
        // Optional<Review> result = dialog.showAndWait();
        // Book book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, Review>() {
            @Override
            public Review call(ButtonType b) {
                int tmp;
                Review result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new Review(
                                ratingField.getValue(),
                                reviewField.getText(),
                                valueOf(reviewDateField.getValue()));
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

    // TODO for the student: check each input separately, to give better
    // feedback to the user
    private boolean isValidData() {
        if(reviewDateField.getValue() == null) {
            return false;
        }
        // if(...) - keep on validating user input...

        return true;
    }

    private void clearFormData() {
        reviewField.setText("");
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
