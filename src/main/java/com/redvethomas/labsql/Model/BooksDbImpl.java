/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redvethomas.labsql.Model;

import com.redvethomas.labsql.View.BookDialog;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private final List<Book> books;
    private Connection connection;

    public BooksDbImpl() {
        books = new ArrayList<>();
        connection = null;
    }

        @Override
        public boolean connect(String database) throws BooksDbException, SQLException {;
            String username = "root";
            String password = "godzhell1";
            String url = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
            try {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected!");
                return true;
            } catch (SQLException e) {
                try {
                    throw e;
                } catch (SQLException es) {
                    es.printStackTrace();
                }
            }
            return false;
    }

    @Override
    public void disconnect() throws BooksDbException {
        if(connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                throw new BooksDbException("did not disconnect", e);
            }
        }
    }

    @Override
    public void addBook(Book book) throws BooksDbException {

    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String query = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author on (BookAuthor.authorId = Author.authorId)" +
                     "WHERE title LIKE '%"+searchTitle+"%'";

            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while(rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int rating = rs.getInt("Rating");
                Date published = rs.getDate("Published");
                String authorName = rs.getString("Name");
                String authorId = rs.getString("AuthorID");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                Book book = new Book(isbn, title, Book.Genre.valueOf(genre),
                        rating, published, authorName, authorId, dateOfBirth);
                result.add(book);
            }
            return result;
        } catch(SQLException e) {
            try {
                throw e;
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                statement.close();
                rs.close();;
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByIsbn(String searchIsbn) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String query = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author on (BookAuthor.authorId = Author.authorId)" +
                    "WHERE title LIKE '%"+searchIsbn+"%'";

            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while(rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int rating = rs.getInt("Rating");
                Date published = rs.getDate("Published");
                String authorName = rs.getString("Name");
                String authorId = rs.getString("AuthorID");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                Book book = new Book(isbn, title, Book.Genre.valueOf(genre),
                        rating, published, authorName, authorId, dateOfBirth);
                result.add(book);
            }
            return result;
        } catch(SQLException e) {
            try {
                throw e;
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                statement.close();
                rs.close();;
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
        }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String query = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author on (BookAuthor.authorId = Author.authorId)" +
                    "WHERE title LIKE '%" + searchAuthor + "%'";

            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int rating = rs.getInt("Rating");
                Date published = rs.getDate("Published");
                String authorName = rs.getString("Name");
                String authorId = rs.getString("AuthorID");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                Book book = new Book(isbn, title, Book.Genre.valueOf(genre),
                        rating, published, authorName, authorId, dateOfBirth);
                result.add(book);
            }
            return result;
        } catch (SQLException e) {
            try {
                throw e;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                statement.close();
                rs.close();
                ;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
