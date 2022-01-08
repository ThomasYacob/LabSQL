package com.redvethomas.labsql.View;


import com.redvethomas.labsql.Model.Book;
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
 * @author Anders Lindström, anderslm@kth.se
 */
public class BookDialog extends Dialog<Book> {

    private final TextField isbnField = new TextField();
    private final TextField titleField = new TextField();
    private final ComboBox<Book.Genre> genreChoice = new ComboBox(FXCollections
            .observableArrayList(Book.Genre.values()));
    private final TextField ratingField = new TextField();
    private final DatePicker publishedField = new DatePicker();
    private final TextField authorName = new TextField();
    private final TextField authorId = new TextField();
    private final DatePicker dateOfBirth = new DatePicker();


    public BookDialog() {
        buildBookDialog();
    }

    private void buildBookDialog() {

        this.setTitle("Add a new book");
        this.setResizable(false); // really?

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Isbn "), 1, 1);
        grid.add(isbnField, 2, 1);
        grid.add(new Label("Title "), 1, 2);
        grid.add(titleField, 2, 2);
        grid.add(new Label("Published "), 1, 3);
        grid.add(publishedField, 2, 3);
        grid.add(new Label("Genre "), 1, 4);
        grid.add(genreChoice, 2, 4);
        grid.add(new Label("Rating "), 1, 5);
        grid.add(ratingField, 2, 5);


        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk
                = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel
                = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // this callback returns the result from our dialog, via
        // Optional<Book> result = dialog.showAndWait();
        // Book book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, Book>() {
            @Override
            public Book call(ButtonType b) {
                Book result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new Book(
                                isbnField.getText(),
                                titleField.getText(),
                                genreChoice.getValue(),
                                ratingField.getAnchor(),
                                valueOf(publishedField.getValue()),
                                authorName.getText(),
                                authorId.getId(),
                                valueOf(dateOfBirth.getValue()));
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
        if (genreChoice.getValue() == null) {
            return false;
        }
        if (!Book.isValidIsbn(isbnField.getText())) {
            System.out.println(isbnField.getText());
            return false;
        }
        // if(...) - keep on validating user input...

        return true;
    }

    private void clearFormData() {
        titleField.setText("");
        isbnField.setText("");
        genreChoice.setValue(null);
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
