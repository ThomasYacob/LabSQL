package com.redvethomas.labsql.Controller;

import com.redvethomas.labsql.Model.*;
import com.redvethomas.labsql.View.BooksPane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    public void connectDatabase() {
        try {
            booksDb.connect("BooksDB");
        } catch (BooksDbException | SQLException e) {
            e.printStackTrace();
        };
    }

    public void disconnectDatabase() {
        try {
            booksDb.disconnect();
        } catch (BooksDbException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        try {
            booksDb.addBook(book);
        } catch (BooksDbException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAuthor(Author author) {
        try {
            booksDb.addAuthor(author);
        } catch (BooksDbException e) {
            e.printStackTrace();
        }
    }

    public void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 0) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        result = booksDb.searchBooksByIsbn(searchFor);
                        break;
                    case Author:
                        result = booksDb.searchBooksByAuthor(searchFor);
                        break;
                    case Rating:
                        result = booksDb.searchBooksByRating(Integer.parseInt(searchFor));
                        break;
                    case Genre:
                        result = booksDb.searchBooksByGenre(searchFor);
                    default:
                        result = new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}
