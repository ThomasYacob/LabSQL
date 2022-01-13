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
 * @author Thomas Yacob & Redve Ahmed
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    /**
     * This is a constructor for the controller
     * @param booksDb the booksDb interface
     * @param booksView the booksView
     */
    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    /**
     * This is a method that connects to the database
     */
    public void connectDatabase() {
        try {
            booksDb.connect("BooksDB");
        } catch (BooksDbException e) {
            e.printStackTrace();
        };
    }

    /**
     * This is a method that disconnects the database
     */
    public void disconnectDatabase() {
        try {
            booksDb.disconnect();
        } catch (BooksDbException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is a method that removes a book from the database
     * @param isbn the isbn string
     */
    public void bookRemoval(String isbn) {
        try {
            booksDb.removeBook(isbn);
        } catch (BooksDbException | SQLException e) {
            booksView.showAlertAndWait("Book not found",ERROR);
        }
    }

    /**
     * This is a method that adds a book into the database
     * @param book the book object
     */
    public void addBook(Book book) {
        new Thread() {
            public void run() {
                try {
                    booksDb.addBook(book);
                } catch (BooksDbException | SQLException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        throw new BooksDbException("1", e);
                                    } catch (BooksDbException e) {
                                        e.printStackTrace();
                                    }
                                    booksView.showAlertAndWait("2", ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    /**
     * This is a method that adds an author into an existing book
     * @param author the author object
     * @param Isbn the Isbn string to connect to BookAuthor
     */
    public void addAuthor(Author author, String Isbn) {
        new Thread() {
            public void run() {
                try {
                    booksDb.addAuthor(author, Isbn);
                } catch (BooksDbException | SQLException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("1", ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    /**
     * This is a method for different types of search modes
     * @param searchFor This is the string that is going to be searched for
     * @param mode This is which mode that is chosen to be searched by
     */
    public void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 0) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByTitle(searchFor);
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.displayBooks(result);
                                                }
                                            }
                                    );
                                } catch (BooksDbException e) {
                                    booksView.showAlertAndWait("", ERROR);
                                }
                            }
                        }.start();
                        break;
                    case ISBN:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByIsbn(searchFor);
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.displayBooks(result);
                                                }
                                            }
                                    );
                                } catch (BooksDbException e) {
                                    booksView.showAlertAndWait("", ERROR);
                                }
                            }
                        }.start();
                        break;
                    case Author:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByAuthor(searchFor);
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.displayBooks(result);
                                                }
                                            }
                                    );
                                } catch (BooksDbException e) {
                                    booksView.showAlertAndWait("", ERROR);
                                }
                            }
                        }.start();
                        break;
                    case Rating:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByRating(Integer.parseInt(searchFor));
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.displayBooks(result);
                                                }
                                            }
                                    );
                                } catch (BooksDbException e) {
                                    booksView.showAlertAndWait("", ERROR);
                                }
                            }
                        }.start();
                        break;
                    case Genre:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByGenre(searchFor);
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.displayBooks(result);
                                                }
                                            }
                                    );
                                } catch (BooksDbException e) {
                                    booksView.showAlertAndWait("", ERROR);
                                }
                            }
                        }.start();
                    default:
                         result = new ArrayList<>();
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
