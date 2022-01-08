/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redvethomas.labsql.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        public boolean connect(String database) throws BooksDbException {;
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
    public void addBook(Book book) throws BooksDbException, SQLException {
        if(book == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);

            statement = connection.prepareStatement("INSERT INTO Book(isbn, title, published) VALUES(?, ?, ?)");
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setDate(3, Date.valueOf(String.valueOf(book.getPublished())));
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Genre VALUES(?, ?)");
            statement.setString(1, book.getGenre().toString());
            statement.setString(2, book.getIsbn());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Rating VALUES(?, ?)");
            statement.setString(1, Integer.toString(book.getRating()));
            statement.setString(2, book.getIsbn());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Author VALUES(?, ?, ?)");
            statement.setString(1, book.getAuthorName());
            statement.setDate(2, Date.valueOf(String.valueOf(book.getAuthors().get(0).getDateOfBirth())));
            statement.setString(3, book.getAuthors().get(0).getAuthorID());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO BookAuthor VALUES(?, ?)");
            statement.setString(1, book.getAuthors().get(0).getAuthorID());
            statement.setString(2, book.getIsbn());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException, SQLException {
        if(author == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);

//            statement = connection.prepareStatement("INSERT INTO Book(isbn, title, published) VALUES (?, ?, ?)");
//            statement.setString(1, book.getIsbn());
//            statement.setString(2, book.getTitle());
//            statement.setDate(3, Date.valueOf(String.valueOf(book.getPublished())));
//            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Author(Name, dateOfBirth, authorId) VALUES(?, ?, ?)");
            statement.setString(1, author.getName());
            statement.setString(2, String.valueOf(author.getDateOfBirth()));
            statement.setString(3, author.getAuthorID());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO BookAuthor(authorId, isbn) VALUES(?, ?)");
            statement.setString(1, author.getAuthorID());
            statement.setString(2, books.get(1).getIsbn());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author ON (BookAuthor.authorId = Author.authorId)" +
                    "WHERE title LIKE '%" + searchTitle + "%'");
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"),
                        rs.getString("Name"), rs.getString("AuthorID"),
                        rs.getDate("dateOfBirth"));
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
            } catch (SQLException e) {
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
            String sql = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author ON (BookAuthor.authorId = Author.authorId)" +
                    "WHERE Book.isbn LIKE '%" + searchIsbn + "%'";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"),
                        rs.getString("Name"), rs.getString("AuthorID"),
                        rs.getDate("dateOfBirth"));
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
            } catch (SQLException e) {
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
            String sql = "SELECT * FROM Author " +
                    "JOIN BookAuthor ON (Author.authorId = BookAuthor.authorId)" +
                    "JOIN Book ON (BookAuthor.isbn = Book.isbn)" +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Author.Name LIKE '%" + searchAuthor + "%'";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"),
                        rs.getString("Name"), rs.getString("AuthorID"),
                        rs.getDate("dateOfBirth"));
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByRating(int searchRating) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String sql = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author ON (BookAuthor.authorId = Author.authorId)" +
                    "WHERE Rating.rating LIKE '%" + searchRating + "%'";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"),
                        rs.getString("Name"), rs.getString("AuthorID"),
                        rs.getDate("dateOfBirth"));
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            String sql = "SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "join BookAuthor ON (Book.isbn = BookAuthor.isbn)" +
                    "join Author ON (BookAuthor.authorId = Author.authorId)" +
                    "WHERE genre LIKE '%" + searchGenre + "%'";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"),
                        rs.getString("Name"), rs.getString("AuthorID"),
                        rs.getDate("dateOfBirth"));
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
