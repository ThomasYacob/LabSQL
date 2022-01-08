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
        } catch (BooksDbException e) {
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
//                } catch (SQLException e) {
//                    javafx.application.Platform.runLater(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    booksView.showAlertAndWait("2", ERROR);
//                                }
//                            }
//                    );
                }
            }
        }.start();
    }

    public void addAuthor(Author author) {
        new Thread() {
            public void run() {
                try {
                    booksDb.addAuthor(author);
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
//                if (result == null || result.isEmpty()) {
//                    booksView.showAlertAndWait(
//                            "No results found.", INFORMATION);
//                } else {
//                    booksView.displayBooks(result);
//                }
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
