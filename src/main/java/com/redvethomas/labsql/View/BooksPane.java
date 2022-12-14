package com.redvethomas.labsql.View;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.redvethomas.labsql.Controller.Controller;
import com.redvethomas.labsql.Model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author Thomas Yacob & Redve Ahmed
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    BooksDbImpl booksDb = new BooksDbImpl(); // model
    private Controller controller;

    private Book books;
    private Author authors;

    private BookDialog bookDialog = new BookDialog();
    private IsbnDialog isbnDialog = new IsbnDialog();
    private AuthorDialog authorDialog = new AuthorDialog();
    private UserDialog userDialog = new UserDialog();
    private ReviewDialog reviewDialog = new ReviewDialog();

    private MenuBar menuBar;

    public BooksPane(BooksDbImpl booksDb) {
        controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING et c.
     */
    public void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, String> authorCol = new TableColumn<>("Authors");
        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Review> reviewCol = new TableColumn<>("Review");
        TableColumn<Book, String> addedByCol = new TableColumn<>("addedBy");
        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol, authorCol, genreCol, reviewCol, addedByCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.14));
        isbnCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.14));
        publishedCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.10));
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.13));
        genreCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.15));
        reviewCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.23));
        addedByCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.11));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        reviewCol.setCellValueFactory(new PropertyValueFactory<>("reviews"));
        addedByCol.setCellValueFactory(new PropertyValueFactory<>("user"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");

        MenuItem exitItem = new MenuItem("Exit");
        EventHandler<ActionEvent> exitHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        };
        exitItem.addEventHandler(ActionEvent.ACTION, exitHandler);

        MenuItem connectItem = new MenuItem("Connect to Db");
        EventHandler<ActionEvent> connectHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<User> result = userDialog.showAndWait();
                User user = result.get();
                controller.connectDatabase(user);
            }
        };
        connectItem.addEventHandler(ActionEvent.ACTION, connectHandler);

        MenuItem disconnectItem = new MenuItem("Disconnect");
        EventHandler<ActionEvent> disconnectHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.disconnectDatabase();
            }
        };
        disconnectItem.addEventHandler(ActionEvent.ACTION, disconnectHandler);

        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addBookItem = new MenuItem("Add Book");
        EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    Optional<Book> result = bookDialog.showAndWait();
                    Book book = result.get();
                    controller.addBook(book);
                }
        };
        addBookItem.addEventHandler(ActionEvent.ACTION, addHandler);

        MenuItem addAuthorItem = new MenuItem("Add author");
        EventHandler<ActionEvent> addAuthorHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> resultIsbn = isbnDialog.showAndWait();
                String Isbn = resultIsbn.get();
                Optional<Author> result = authorDialog.showAndWait();
                Author author = result.get();
                controller.addAuthor(author,Isbn);
            }
        };
        addAuthorItem.addEventHandler(ActionEvent.ACTION, addAuthorHandler);

        MenuItem reviewItem = new MenuItem("Review a book");
        EventHandler<ActionEvent> reviewHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> resultIsbn = isbnDialog.showAndWait();
                String isbn = resultIsbn.get();
                Optional<Review> result = reviewDialog.showAndWait() ;
                Review review = result.get();
                controller.addReview(review, isbn);
            }
        };
        reviewItem.addEventHandler(ActionEvent.ACTION, reviewHandler);

        MenuItem removeItem = new MenuItem("Remove");
        EventHandler<ActionEvent> addRemoveHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> resultIsbn = isbnDialog.showAndWait();
                String isbn = resultIsbn.get();
                controller.bookRemoval(isbn);
            }
        };
        removeItem.addEventHandler(ActionEvent.ACTION, addRemoveHandler);

        manageMenu.getItems().addAll(addBookItem, addAuthorItem, reviewItem, removeItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, manageMenu);
    }
}
