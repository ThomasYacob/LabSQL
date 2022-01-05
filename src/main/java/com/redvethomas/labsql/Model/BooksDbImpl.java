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
    private List<Author> authors;
    private Connection connection;

    public BooksDbImpl() {
//        books = Arrays.asList(DATA);
        books = new ArrayList<>();
        connection = null;
    }

        @Override
        public boolean connect(String database) throws BooksDbException, SQLException {;
            String username = "Thomas";
            String password = "Thoomas123!";
            String url = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
            try {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                connection = DriverManager.getConnection(server, username, password);
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected!");
                return true;
            } catch (SQLException e) {
                try {
                    throw e;
                } catch (SQLException es) {
                    es.printStackTrace();
                }
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
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
        // mock implementation
    }

    @Override
    public void addBook(Book book) throws BooksDbException {

    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        // mock implementation
        // NB! Your implementation should select the books matching
        // the search string via a query to a database (not load all books from db)
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String query = "Select * from Book WHERE title like ?";
//            String query = "Select * " + "FROM (book " + "INNER JOIN BooksDB.Book ON (book.ISBN=writtenby.ISBN"
//                    + "INNER JOIN author ON (author.authorID=writtenby.authorID)" + "WHERE book.title LIKE '%"
//                    + searchTitle + "%'";
            statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTitle + "%");
            rs = statement.executeQuery();
            while(rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int rating = rs.getInt("Rating");
                String name = rs.getString("Name");
                String authorId = rs.getString("AuthorID");
                Date dateOfBirth = rs.getDate("Date of birth");
                Book book = new Book(isbn, title, Book.Genre.valueOf(genre), rating, name, authorId, dateOfBirth);
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
            for (Book book: books) {
                if(book.getIsbn().matches(searchIsbn)) {
                    result.add(book);
                }
            }
            return result;
        }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        searchAuthor = searchAuthor.toLowerCase();
        for(Book book : books) {
            if(book.getAuthors().toString().contains(searchAuthor)) {
                result.add(book);
            }
        }
        return result;
    }
}
