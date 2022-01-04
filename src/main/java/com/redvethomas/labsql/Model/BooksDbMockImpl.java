/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redvethomas.labsql.Model;

import javafx.application.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbMockImpl implements BooksDbInterface {

    private final List<Book> books;
    private List<Author> authors;
    private Connection connection = null;

    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }
//    private String username = args[0];
//    private String password = args[1];
//    private String[] args;



    private static final String username = "root";
    private static final String password = "godzhell1";

    private String url = "jdbc:mysql://localhost:3306/";

        @Override
        public boolean connect(String database) throws BooksDbException, SQLException {
            String url = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
//                connection = DriverManager.getConnection(server, username, password);
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                        System.out.println("Connection closed.");
                    }
                } catch (SQLException e) {

                }
            }
        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
//        if(connection != null) {
//            try {
//                connection.close();
//                connection = null;
//                System.out.println("Connection closed.");
//            } catch (SQLException e) {
//                throw new BooksDbException("did not disconnect", e);
//            }
//        }
        // mock implementation
    }

    /*
        @Override
        public boolean connect(String database) throws BooksDbException {
            if(connection == null) {
                try {
    //                connection = DriverManager.getConnection(url + database + "?UseClientEnc=UTF8");
                    connection = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected!");
                } catch (SQLException e) {
                    throw new BooksDbException("did not connect");
                }
            }

            return true;
            */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        // mock implementation
        // NB! Your implementation should select the books matching
        // the search string via a query to a database (not load all books from db)
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTitle)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByIsbn(String searchIsbn) throws BooksDbException {
            List<Book> result = new ArrayList<>();
            for (Book book: books) {
                if(book.getIsbn().matches(searchIsbn)) {
                    result.add(book);
                }
            }
            return result;
        }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
//        List<Author> result = (ArrayList<Author>)

        return null;
    }

    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1), Book.Genre.DYSTOPIAN, 1),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1), Book.Genre.FANTASY, 2),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1), Book.Genre.FANTASY, 3),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1), Book.Genre.FANTASY, 4),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1), Book.Genre.HORROR, 5),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1), Book.Genre.HORROR, 5),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1), Book.Genre.LITERARY_FICTION, 5),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1), Book.Genre.MYSTERY, 2),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1), Book.Genre.ROMANCE, 1),
    };
}
