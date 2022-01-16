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
    private User currentUser;

    /**
     * The constructor for the main model
     */
    public BooksDbImpl() {
        books = new ArrayList<>();
        connection = null;
    }

    /**
     * This method is used for connecting to a database
     *
     * @param database which database to be connected to
     * @return returns true if connected
     * @throws BooksDbException Throws an exception if error occurs
     */
    @Override
    public boolean connect(String database, User user) throws BooksDbException {
        String url = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
        try {
            this.currentUser = user;
            connection = DriverManager.getConnection(url, user.getUsername(), user.getPassword());
            System.out.println("Connected!");
            return true;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        }
    }

    /**
     * This is a method that disconnects the active program from the database
     *
     * @throws BooksDbException Throws an exception if error occurs
     */
    @Override
    public void disconnect() throws BooksDbException {
        if (connection != null) {
            try {
                this.currentUser = null;
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                throw new BooksDbException(e.getSQLState());
            }
        }
    }

    /**
     * This is a method that adds a book into the database
     *
     * @param book The book object that is added
     * @throws BooksDbException if the book is not added successfully, a BooksDbException is thrown
     * @throws SQLException     Throws a SQLException if error occurs
     */
    @Override
    public void addBook(Book book) throws BooksDbException, SQLException {
        if (book == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);
            book.setUser(currentUser);

            statement = connection.prepareStatement("INSERT INTO Book(isbn, addedBy, title, published) VALUES(?, ?, ?, ?)");
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getUser().getUsername());
            statement.setString(3, book.getTitle());
            statement.setDate(4, Date.valueOf(String.valueOf(book.getPublished())));
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Genre VALUES(?, ?)");
            statement.setString(1, book.getGenre().toString());
            statement.setString(2, book.getIsbn());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Author (addedBy, name, dateofbirth) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, book.getUser().getUsername());
            statement.setString(2, book.getAuthors().get(0).getAuthorName());
            statement.setDate(3, Date.valueOf(String.valueOf(book.getAuthors().get(0).getDateOfBirth())));
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                book.getAuthors().get(0).setAuthorId(rs.getInt(1));
            }

            statement = connection.prepareStatement("INSERT INTO BookAuthor VALUES(?, ?)");
            statement.setInt(1, book.getAuthors().get(0).getAuthorID());
            statement.setString(2, book.getIsbn());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                throw new BooksDbException(e.getSQLState());
            }
        }
    }

    /**
     * This is a method that adds an author into an existing book
     *
     * @param author The author object that is being added
     * @param Isbn   This is the ISBN of the book
     * @throws BooksDbException if an author is not added successfully, a BooksDbException is thrown
     * @throws SQLException     Throws a SQLException if error occurs
     */
    @Override
    public void addAuthor(Author author, String Isbn) throws BooksDbException, SQLException {
        if (author == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);
            author.setUser(currentUser);
            statement = connection.prepareStatement("INSERT INTO Author(addedBy, Name, dateOfBirth) VALUES(?, ?, ?)", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, author.getUser().getUsername());
            statement.setString(2, author.getAuthorName());
            statement.setString(3, String.valueOf(author.getDateOfBirth()));
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            int key = -1;
            if (rs.next()) {
                key = rs.getInt(1);
            }

            statement = connection.prepareStatement("INSERT INTO BookAuthor(authorId, isbn) VALUES(?, ?)");
            statement.setInt(1, key);
            statement.setString(2, Isbn);
            statement.executeUpdate();

            statement = connection.prepareStatement("SELECT CURRENT_USER()");

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                throw new BooksDbException(e.getSQLState());
            }
        }
    }

    /**
     * @param review The review object of the book
     * @param isbn   This is the ISBN of the book
     * @throws BooksDbException if a review is not added successfully, a BooksDbException is thrown
     * @throws SQLException     Throws a SQLException if error occurs
     */
    @Override
    public void addReview(Review review, String isbn) throws BooksDbException, SQLException {
        if(review == null) {
            throw new BooksDbException("");
        }
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);
            review.setUser(currentUser);
            statement = connection.prepareStatement("INSERT INTO Rating VALUES(?, ?, ?, ?, ?)");
            statement.setString(1, isbn);
            statement.setString(2, review.getUser().getUsername());
            statement.setString(3, Integer.toString(review.getRating()));
            statement.setString(4, review.getStoryLine());
            statement.setDate(5, Date.valueOf(String.valueOf(review.getReviewDate())));
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                throw new BooksDbException(e.getSQLState());
            }
        }
    }

    /**
     * This is a method that removes a specific book from the database
     *
     * @param isbn This is the ISBN of the book
     * @throws BooksDbException if the book is not removed successfully, a BooksDbException is thrown
     * @throws SQLException     Throws a SQLException if error occurs
     */
    @Override
    public void removeBook(String isbn) throws BooksDbException, SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement("DELETE FROM Rating WHERE isbn = " + isbn);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM Genre WHERE isbn = " + isbn);
            statement.executeUpdate();

            statement = connection.prepareStatement("SELECT * FROM BookAuthor WHERE isbn = " + isbn);
            rs = statement.executeQuery();
            ArrayList<Integer> tmpAuthorID = new ArrayList<>();
            while (rs.next()) {
                tmpAuthorID.add(rs.getInt("AuthorID"));
            }

            statement = connection.prepareStatement("DELETE FROM BookAuthor WHERE isbn = " + isbn);
            statement.executeUpdate();

            for (int i = 0; i < tmpAuthorID.size(); i++) {
                statement = connection.prepareStatement("DELETE FROM Author WHERE authorId = " + tmpAuthorID.get(i));
                statement.executeUpdate();
            }

            statement = connection.prepareStatement("DELETE FROM Book WHERE isbn = " + isbn);
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                statement.close();
            } catch (SQLException e) {
                throw new BooksDbException(e.getSQLState());
            }
        }
    }

    /**
     * This is a method that searches for books by the title
     *
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

            result = getAuthorsAndUsersFromSearch(result, rs, statement);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
    }

    /**
     * This is a method that searches for books by isbn
     *
     * @param searchISBN the inserted isbn string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByIsbn(String searchISBN) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Book.isbn LIKE '%" + searchISBN + "%'");
            rs = statement.executeQuery();

            result = getAuthorsAndUsersFromSearch(result, rs, statement);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
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
                    "WHERE Author.Name LIKE '%" + searchAuthor + "%'");
            rs = statement.executeQuery();

            List<String> tmpID1 = new ArrayList<>();

            while (rs.next()) {
                tmpID1.add(rs.getString("ISBN"));
            }

            for (int i = 0; i < tmpID1.size(); i++) {
                statement = connection.prepareStatement("SELECT * FROM Book " +
                        "join Genre ON (Book.isbn = Genre.isbn)" +
                        "join Rating ON (Book.isbn = Rating.isbn)" +
                        "WHERE Book.isbn LIKE '%" + tmpID1.get(i) + "%'");
                rs = statement.executeQuery();
                while (rs.next()) {
                    Book book = new Book(
                            rs.getString("ISBN"),
                            rs.getString("Title"),
                            Book.Genre.valueOf(rs.getString("Genre")),
                            rs.getDate("Published"),
                            rs.getString("addedBy"));
                    result.add(book);
                }
            }
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++) {
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<>();
                while (rs.next()) {
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++) {
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()) {
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"),
                                rs.getString("addedBy"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();


                }
                statement = connection.prepareStatement("SELECT * FROM Rating " +
                        "WHERE isbn = " + result.get(i).getIsbn());
                rs = statement.executeQuery();
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("Rating"),
                            rs.getString("review"),
                            rs.getDate("reviewDate"),
                            new User(rs.getString("addedBy")));
                    result.get(i).addReview(review);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
    }

    /**
     * This is a method that searches for books by rating
     * @param searchRating the inserted rating integer
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByRating ( int searchRating) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {

            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Rating.rating LIKE '%" + searchRating + "%'");
            rs = statement.executeQuery();
            result = getAuthorsAndUsersFromSearch(result, rs, statement);

            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
    }

    /**
     * This is a method that searches for books by genre
     * @param searchGenre the inserted genre string
     * @return returns the corresponding book object
     * @throws BooksDbException if the book is not found, a BooksDbException is thrown
     */
    @Override
    public List<Book> searchBooksByGenre (String searchGenre) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE genre LIKE '%" + searchGenre + "%'");
            rs = statement.executeQuery();

            result = getAuthorsAndUsersFromSearch(result, rs, statement);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
    }

    private List<Book> getAuthorsAndUsersFromSearch(List<Book> result, ResultSet rs, PreparedStatement statement) throws BooksDbException {
        try {
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        Book.Genre.valueOf(rs.getString("Genre")),
                        rs.getDate("Published"),
                        rs.getString("addedBy"));
                result.add(book);
            }
            statement.clearParameters();
            for (int i = 0; i < result.size(); i++) {
                statement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = statement.executeQuery();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while (rs.next()) {
                    tmpID.add(rs.getInt("AuthorID"));
                }
                statement.clearParameters();

                for (int j = 0; j < tmpID.size(); j++) {
                    statement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = statement.executeQuery();
                    while (rs.next()) {
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"),
                                rs.getString("addedBy"));
                        result.get(i).addAuthor(author);
                    }
                    statement.clearParameters();
                }
                for (int k = 0; k < result.size(); k++) {
                    statement = connection.prepareStatement("SELECT * FROM Rating " +
                            "WHERE isbn = " + result.get(k).getIsbn());
                    rs = statement.executeQuery();

                    while (rs.next()) {
                        Review review = new Review(
                                rs.getInt("Rating"),
                                rs.getString("review"),
                                rs.getDate("reviewDate"),
                                new User(rs.getString("addedBy")));
                        result.get(k).addReview(review);
                    }
                }
                statement = connection.prepareStatement("SELECT * FROM Rating " +
                        "WHERE isbn = " + result.get(i).getIsbn());
                rs = statement.executeQuery();
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("Rating"),
                            rs.getString("review"),
                            rs.getDate("reviewDate"),
                            new User(rs.getString("addedBy")));
                    result.get(i).addReview(review);
                }
            }
            return result;
        } catch(SQLException e){
            throw new BooksDbException();
        }
    }
}