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
 * @author Thomas Yacob & Redve Ahmed
 */
public class BooksDbImpl implements BooksDbInterface {

    private final List<Book> books;

    private Connection connection;

    public BooksDbImpl() {
        books = new ArrayList<>();
        connection = null;
    }

    /**
     * This method is used for connecting to a database
     * @param database which database to be connected to
     * @return returns
     * @throws BooksDbException
     */
        @Override
        public boolean connect(String database) throws BooksDbException {
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

    /**
     * This is a method that disconnects the active program from the database
     * @throws BooksDbException
     */
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

    /**
     * This is a method that adds a book into the database
     * @param book The book object that is added
     * @throws BooksDbException if the book is not added successfully, a BooksDbException is thrown
     * @throws SQLException
     */
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

            statement = connection.prepareStatement("INSERT INTO Author (name, dateofbirth) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, book.getAuthors().get(0).getAuthorName());
            statement.setDate(2, Date.valueOf(String.valueOf(book.getAuthors().get(0).getDateOfBirth())));
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()) {
                    book.getAuthors().get(0).setAuthorId(rs.getInt(1));
            }

            statement = connection.prepareStatement("INSERT INTO BookAuthor VALUES(?, ?)");
            statement.setInt(1, book.getAuthors().get(0).getAuthorID());
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

    /**
     * This is a method that adds an author into an existing book
     * @param author The author object that is being added
     * @param Isbn This is the ISBN of the book
     * @throws BooksDbException if an author is not added successfully, a BooksDbException is thrown
     * @throws SQLException
     */
    @Override
    public void addAuthor(Author author, String Isbn) throws BooksDbException, SQLException {
        if(author == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);

            statement = connection.prepareStatement("INSERT INTO Author(Name, dateOfBirth) VALUES(?, ?)", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, author.getAuthorName());
            statement.setString(2, String.valueOf(author.getDateOfBirth()));
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            int key = -1;
            if(rs.next()) {
                key = rs.getInt(1);
            }

            statement = connection.prepareStatement("INSERT INTO BookAuthor(authorId, isbn) VALUES(?, ?)");
            statement.setInt(1, key);
            statement.setString(2, Isbn);
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

    /**
     * This is a method that removes a specifik book from the database
     * @param isbn This is the ISBN of the book
     * @throws BooksDbException if the book is not removed successfully, a BooksDbException is thrown
     * @throws SQLException
     */
    @Override
    public void removeBook(String isbn) throws BooksDbException, SQLException {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try{
                connection.setAutoCommit(false);
                statement = connection.prepareStatement("DELETE FROM Rating WHERE isbn = " + isbn);
                statement.executeUpdate();

                statement = connection.prepareStatement("DELETE FROM Genre WHERE isbn = " + isbn);
                statement.executeUpdate();

                statement = connection.prepareStatement("SELECT * FROM BookAuthor WHERE isbn = " + isbn);
                rs = statement.executeQuery();
                ArrayList<Integer> tmpAuthorID = new ArrayList<>();
                while (rs.next()){
                    tmpAuthorID.add(rs.getInt("AuthorID"));
                }

                statement = connection.prepareStatement("DELETE FROM BookAuthor WHERE isbn = " + isbn);
                statement.executeUpdate();

//                for (Integer integer : tmpAuthorID) {
                for (int i = 0; i < tmpAuthorID.size(); i++){
                    statement = connection.prepareStatement("DELETE FROM Author WHERE authorId = " + tmpAuthorID.get(i));
                    statement.executeUpdate();
                }

                statement = connection.prepareStatement("DELETE FROM Book WHERE isbn = " + isbn);
                statement.executeUpdate();

                connection.commit();
            }  catch (SQLException e) {
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

    /**
     * This is a method that searches for books by the title
     * @param searchTitle the inserted title string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE title LIKE '%" + searchTitle + "%'");
            rs = statement.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"));
                result.add(book);
            }
            System.out.println(result);
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++){
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while(rs.next()){
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();
                
                for (int j = 0; j < tmpID.size(); j++){
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()){
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
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

    /**
     * This is a method that searches for books by isbn
     * @param searchIsbn the inserted isbn string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByIsbn(String searchIsbn) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Book.isbn LIKE '%" + searchIsbn + "%'");
            rs = statement.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"));
                result.add(book);
            }
            System.out.println(result);
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++){
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while(rs.next()){
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++){
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()){
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
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

    /**
     * This is a method that searches for books by the author
     * @param searchAuthor the inserted author string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Author " +
                    "join BookAuthor ON (Author.authorId = BookAuthor.authorId)" +
//                    "join Book ON (BookAuthor.isbn = Book.isbn)" +
                    "WHERE Author.Name LIKE '%" + searchAuthor + "%'");
            rs = statement.executeQuery();

            List<String> tmpID1 = new ArrayList<>();

            while (rs.next()){
                tmpID1.add(rs.getString("ISBN"));
            }

            System.out.println(tmpID1);

            for (int i = 0; i < tmpID1.size() ; i++) {
                statement = connection.prepareStatement("SELECT * FROM Book " +
                        "join Genre ON (Book.isbn = Genre.isbn)" +
                        "join Rating ON (Book.isbn = Rating.isbn)" +
//                        "join BookAuthor on (Book.isbn = BookAuthor.isbn)" +
                        "WHERE Book.isbn LIKE '%" + tmpID1.get(i)  + "%'");
                rs = statement.executeQuery();
                while (rs.next()) {
                    Book book = new Book(
                            rs.getString("ISBN"),
                            rs.getString("Title"),
                            Book.Genre.valueOf(rs.getString("Genre")),
                            rs.getInt("Rating"), rs.getDate("Published"));
                    result.add(book);
                }
            }
            System.out.println(result);
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++){
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while(rs.next()){
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++){
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()){
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
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

    /**
     * This is a method that searches for books by rating
     * @param searchRating the inserted rating integer
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByRating(int searchRating) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Rating.rating LIKE '%" + searchRating + "%'");
            rs = statement.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"));
                result.add(book);
            }
            System.out.println(result);
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++){
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while(rs.next()){
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++){
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()){
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
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

    /**
     * This is a method that searches for books by genre
     * @param searchGenre the inserted genre string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE genre LIKE '%" + searchGenre + "%'");
            rs = statement.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getInt("Rating"), rs.getDate("Published"));
                result.add(book);
            }
            System.out.println(result);
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++){
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while(rs.next()){
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++){
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()){
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
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
