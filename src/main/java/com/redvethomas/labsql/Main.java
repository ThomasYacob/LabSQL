package com.redvethomas.labsql;

import com.redvethomas.labsql.Model.BooksDbException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.redvethomas.labsql.Model.BooksDbImpl;
import com.redvethomas.labsql.View.BooksPane;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        BooksDbImpl booksDb = new BooksDbImpl(); // model
        // Don't forget to connect to the db, somewhere...
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
