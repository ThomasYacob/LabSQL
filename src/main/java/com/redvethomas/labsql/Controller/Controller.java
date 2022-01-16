package com.redvethomas.labsql.Controller;

import com.redvethomas.labsql.Model.*;
import com.redvethomas.labsql.View.BooksPane;
import javafx.application.Platform;

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
    public void connectDatabase(User user) {
        new Thread() {
            public void run() {
                try {
                    booksDb.connect("BooksDB", user);
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    /**
     * This is a method that disconnects the database
     */
    public void disconnectDatabase() {
        new Thread() {
            public void run() {
                try {
                    booksDb.disconnect();
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    /**
     * This is a method that removes a book from the database
     * @param isbn the isbn string
     */
    public void bookRemoval(String isbn) {
        new Thread() {
            public void run() {
                try {
                    booksDb.removeBook(isbn);
                } catch (BooksDbException | SQLException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("Book not found", ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
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
                                    booksView.showAlertAndWait("No permission to add a book", ERROR);
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
                                    booksView.showAlertAndWait("No permission to add an author", ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    /**
     * This is a method that adds a review to a book
     * @param review the review object
     * @param isbn the isbn string to connect to the book object
     */
    public void addReview(Review review, String isbn) {
        new Thread() {
            public void run() {
                try {
                    booksDb.addReview(review, isbn);
                } catch (BooksDbException | SQLException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("No permission to add a review", ERROR);
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
                                    booksView.displayBooks(result);
                                } catch (BooksDbException | NullPointerException e) {
                                    Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                                }
                                            }
                                    );
                                }
                            }
                        }.start();
                        break;
                    case ISBN:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByIsbn(searchFor);
                                    booksView.displayBooks(result);
                                } catch (BooksDbException | NullPointerException e) {
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                                }
                                            }
                                    );
                                }
                            }
                        }.start();
                        break;
                    case Author:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByAuthor(searchFor);
                                    booksView.displayBooks(result);
                                } catch (BooksDbException | NullPointerException e) {
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                                }
                                            }
                                    );
                                }
                            }
                        }.start();
                        break;
                    case Rating:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByRating(Integer.parseInt(searchFor));
                                    booksView.displayBooks(result);
                                } catch (BooksDbException | NullPointerException e) {
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                                }
                                            }
                                    );
                                }
                            }
                        }.start();
                        break;
                    case Genre:
                        new Thread() {
                            public void run() {
                                try {
                                    List<Book> result = booksDb.searchBooksByGenre(searchFor);
                                    booksView.displayBooks(result);
                                } catch (BooksDbException | NullPointerException e) {
                                    javafx.application.Platform.runLater(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    booksView.showAlertAndWait(e.getLocalizedMessage(), ERROR);
                                                }
                                            }
                                    );
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

}
