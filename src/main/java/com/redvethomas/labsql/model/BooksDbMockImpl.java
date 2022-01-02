/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redvethomas.labsql.model;

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
    static final String username = "root";
    static final String password = "root";

    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/";
        try{
//            connection = DriverManager.getConnection(url + database + "?UseClientEnc=UTF8");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected!");
        } catch (SQLException e) {
            throw new BooksDbException("lol", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                throw new BooksDbException("Lol",e);
            }
        }

        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
        // mock implementation
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
            throws BooksDbException {
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

    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1), Genre.DYSTOPIAN, 1),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1), Genre.FANTASY, 2),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1), Genre.FANTASY, 3),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1), Genre.FANTASY, 4),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1), Genre.HORROR, 5),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1), Genre.HORROR, 5),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1), Genre.LITERARY_FICTION, 5),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1), Genre.MYSTERY, 2),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1), Genre.ROMANCE, 1),
    };
}
