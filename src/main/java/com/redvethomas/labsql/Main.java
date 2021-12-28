package com.redvethomas.labsql;

import com.redvethomas.labsql.model.Book;
import com.redvethomas.labsql.model.BooksDbException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.redvethomas.labsql.model.BooksDbMockImpl;
import com.redvethomas.labsql.view.BooksPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        BooksDbMockImpl booksDb = new BooksDbMockImpl(); // model
        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws BooksDbException {

        launch(args);
    }
}
