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
     * @throws SQLException Throws a SQLException if error occurs
     */
    @Override
    public void addBook(Book book) throws BooksDbException, SQLException {
        if (book == null) {
            throw new BooksDbException("");
        }
        PreparedStatement bookStatement = null;
        PreparedStatement genreStatement = null;
        PreparedStatement authorStatement = null;
        PreparedStatement bookAuthorStatement = null;
        try {
            connection.setAutoCommit(false);
            book.setUser(currentUser);

            bookStatement = connection.prepareStatement("INSERT INTO Book(isbn, addedBy, title, published) VALUES(?, ?, ?, ?)");
            bookStatement.setString(1, book.getIsbn());
            bookStatement.setString(2, book.getUser().getUsername());
            bookStatement.setString(3, book.getTitle());
            bookStatement.setDate(4, Date.valueOf(String.valueOf(book.getPublished())));
            bookStatement.executeUpdate();
            bookStatement.clearParameters();

            genreStatement = connection.prepareStatement("INSERT INTO Genre VALUES(?, ?)");
            genreStatement.setString(1, book.getGenre().toString());
            genreStatement.setString(2, book.getIsbn());
            genreStatement.executeUpdate();
            genreStatement.clearParameters();

            authorStatement = connection.prepareStatement("INSERT INTO Author (addedBy, name, dateofbirth) VALUES(?, ?, ?)", authorStatement.RETURN_GENERATED_KEYS);
            authorStatement.setString(1, book.getUser().getUsername());
            authorStatement.setString(2, book.getAuthors().get(0).getAuthorName());
            authorStatement.setDate(3, Date.valueOf(String.valueOf(book.getAuthors().get(0).getDateOfBirth())));
            authorStatement.executeUpdate();

            ResultSet rs = authorStatement.getGeneratedKeys();
            if (rs.next()) {
                book.getAuthors().get(0).setAuthorId(rs.getInt(1));
            }
            authorStatement.clearParameters();

            bookAuthorStatement = connection.prepareStatement("INSERT INTO BookAuthor VALUES(?, ?)");
            bookAuthorStatement.setInt(1, book.getAuthors().get(0).getAuthorID());
            bookAuthorStatement.setString(2, book.getIsbn());
            bookAuthorStatement.executeUpdate();
            bookAuthorStatement.clearParameters();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                bookStatement.close();
                genreStatement.close();
                authorStatement.close();
                bookAuthorStatement.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException(e.getLocalizedMessage());
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
        PreparedStatement authorStatement = null;
        PreparedStatement bookAuthorStatement = null;
        try {
            connection.setAutoCommit(false);
            author.setUser(currentUser);

            authorStatement = connection.prepareStatement("INSERT INTO Author(addedBy, Name, dateOfBirth) VALUES(?, ?, ?)", authorStatement.RETURN_GENERATED_KEYS);
            authorStatement.setString(1, author.getUser().getUsername());
            authorStatement.setString(2, author.getAuthorName());
            authorStatement.setString(3, String.valueOf(author.getDateOfBirth()));
            authorStatement.executeUpdate();

            ResultSet rs = authorStatement.getGeneratedKeys();
            int key = -1;
            if (rs.next()) {
                key = rs.getInt(1);
            }
            authorStatement.clearParameters();

            bookAuthorStatement = connection.prepareStatement("INSERT INTO BookAuthor(authorId, isbn) VALUES(?, ?)");
            bookAuthorStatement.setInt(1, key);
            bookAuthorStatement.setString(2, Isbn);
            bookAuthorStatement.executeUpdate();
            bookAuthorStatement.clearParameters();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                authorStatement.close();
                bookAuthorStatement.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException(e.getLocalizedMessage());
            }
        }
    }

    /**
     * @param review The review object of the book
     * @param isbn This is the ISBN of the book
     * @throws BooksDbException if a review is not added successfully, a BooksDbException is thrown
     * @throws SQLException Throws a SQLException if error occurs
     */
    @Override
    public void addReview(Review review, String isbn) throws BooksDbException, SQLException {
        if(review == null) {
            throw new BooksDbException("");
        }
        PreparedStatement ratingStatement = null;
        try {
            connection.setAutoCommit(false);
            review.setUser(currentUser);
            ratingStatement = connection.prepareStatement("INSERT INTO Rating VALUES(?, ?, ?, ?, ?)");
            ratingStatement.setString(1, isbn);
            ratingStatement.setString(2, review.getUser().getUsername());
            ratingStatement.setString(3, Integer.toString(review.getRating()));
            ratingStatement.setString(4, review.getStoryLine());
            ratingStatement.setDate(5, Date.valueOf(String.valueOf(review.getReviewDate())));
            ratingStatement.executeUpdate();
            ratingStatement.clearParameters();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                ratingStatement.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException(e.getLocalizedMessage());
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
        PreparedStatement ratingStatement = null;
        PreparedStatement bookStatement = null;
        PreparedStatement genreStatement = null;
        PreparedStatement bookAuthorStatement = null;
        PreparedStatement authorStatement = null;
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            ratingStatement = connection.prepareStatement("DELETE FROM Rating WHERE isbn = " + isbn);
            ratingStatement.executeUpdate();
            ratingStatement.clearParameters();

            genreStatement = connection.prepareStatement("DELETE FROM Genre WHERE isbn = " + isbn);
            genreStatement.executeUpdate();
            genreStatement.clearParameters();

            bookAuthorStatement = connection.prepareStatement("SELECT * FROM BookAuthor WHERE isbn = " + isbn);
            rs = bookAuthorStatement.executeQuery();
            bookAuthorStatement.clearParameters();

            ArrayList<Integer> tmpAuthorID = new ArrayList<>();
            while (rs.next()) {
                tmpAuthorID.add(rs.getInt("AuthorID"));
            }

            bookAuthorStatement = connection.prepareStatement("DELETE FROM BookAuthor WHERE isbn = " + isbn);
            bookAuthorStatement.executeUpdate();
            bookAuthorStatement.clearParameters();

            for (int i = 0; i < tmpAuthorID.size(); i++) {
                authorStatement = connection.prepareStatement("DELETE FROM Author WHERE authorId = " + tmpAuthorID.get(i));
                authorStatement.executeUpdate();
            }
            authorStatement.clearParameters();

            bookStatement = connection.prepareStatement("DELETE FROM Book WHERE isbn = " + isbn);
            bookStatement.executeUpdate();
            bookStatement.clearParameters();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                connection.setAutoCommit(true);
                ratingStatement.close();
                bookStatement.close();
                genreStatement.close();
                bookAuthorStatement.close();
                authorStatement.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException(e.getLocalizedMessage());
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
        PreparedStatement bookStatement = null;
        ResultSet rs = null;
        try {
            bookStatement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE title LIKE '%" + searchTitle + "%'");
            rs = bookStatement.executeQuery();

            result = getAuthorsAndUsersFromSearch(result, rs, bookStatement);
            bookStatement.clearParameters();
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                bookStatement.close();
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
        PreparedStatement bookStatement = null;
        try {
            bookStatement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Book.isbn LIKE '%" + searchISBN + "%'");
            rs = bookStatement.executeQuery();

            result = getAuthorsAndUsersFromSearch(result, rs, bookStatement);
            bookStatement.clearParameters();
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                bookStatement.close();
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
        PreparedStatement authorStatement = null;
        PreparedStatement bookStatement = null;
        PreparedStatement bookAuthorStatement = null;
        PreparedStatement ratingStatement = null;
        try {
            authorStatement = connection.prepareStatement("SELECT * FROM Author " +
                    "join BookAuthor ON (Author.authorId = BookAuthor.authorId)" +
                    "WHERE Author.Name LIKE '%" + searchAuthor + "%'");
            rs = authorStatement.executeQuery();
            authorStatement.clearParameters();

            List<String> tmpID1 = new ArrayList<>();
            while (rs.next()) {
                tmpID1.add(rs.getString("ISBN"));
            }

            for (int i = 0; i < tmpID1.size(); i++) {
                bookStatement = connection.prepareStatement("SELECT * FROM Book " +
                        "join Genre ON (Book.isbn = Genre.isbn)" +
                        "join Rating ON (Book.isbn = Rating.isbn)" +
                        "WHERE Book.isbn LIKE '%" + tmpID1.get(i) + "%'");
                rs = bookStatement.executeQuery();
                bookStatement.clearParameters();
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

            for (int i = 0; i < result.size(); i++) {
                bookAuthorStatement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = bookAuthorStatement.executeQuery();
                bookAuthorStatement.clearParameters();

                ArrayList<Integer> tmpID = new ArrayList<>();
                while (rs.next()) {
                    tmpID.add(rs.getInt("AuthorID"));
                }

                for (int j = 0; j < tmpID.size(); j++) {
                    authorStatement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = authorStatement.executeQuery();
                    authorStatement.clearParameters();

                    while (rs.next()) {
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"),
                                rs.getString("addedBy"));
                        result.get(i).addAuthor(author);
                    }
                }

                ratingStatement = connection.prepareStatement("SELECT * FROM Rating " +
                        "WHERE isbn = " + result.get(i).getIsbn());
                rs = ratingStatement.executeQuery();
                ratingStatement.clearParameters();

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
                authorStatement.close();
                bookStatement.close();
                bookAuthorStatement.close();
                ratingStatement.close();
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
        PreparedStatement bookStatement = null;
        try {

            bookStatement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE Rating.rating LIKE '%" + searchRating + "%'");
            rs = bookStatement.executeQuery();
            result = getAuthorsAndUsersFromSearch(result, rs, bookStatement);
            bookStatement.clearParameters();
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                bookStatement.close();
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
        PreparedStatement bookStatement = null;
        try {
            bookStatement = connection.prepareStatement("SELECT * FROM Book " +
                    "join Genre ON (Book.isbn = Genre.isbn)" +
                    "join Rating ON (Book.isbn = Rating.isbn)" +
                    "WHERE genre LIKE '%" + searchGenre + "%'");
            rs = bookStatement.executeQuery();
            result = getAuthorsAndUsersFromSearch(result, rs, bookStatement);
            bookStatement.clearParameters();
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getSQLState());
        } finally {
            try {
                bookStatement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Not logged in");
            }
        }
    }

    private List<Book> getAuthorsAndUsersFromSearch(List<Book> result, ResultSet rs, PreparedStatement statement) throws BooksDbException {
        PreparedStatement bookAuthorStatement = null;
        PreparedStatement authorStatement = null;
        PreparedStatement ratingStatement = null;
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

            for (int i = 0; i < result.size(); i++) {
                bookAuthorStatement = connection.prepareStatement("SELECT * FROM BookAuthor " +
                        "WHERE isbn = " +
                        result.get(i).getIsbn());
                rs = bookAuthorStatement.executeQuery();
                bookAuthorStatement.clearParameters();
                ArrayList<Integer> tmpID = new ArrayList<Integer>();
                while (rs.next()) {
                    tmpID.add(rs.getInt("AuthorID"));
                }

                for (int j = 0; j < tmpID.size(); j++) {
                    authorStatement = connection.prepareStatement("SELECT * FROM Author " +
                            "WHERE authorId = " + tmpID.get(j));
                    rs = authorStatement.executeQuery();
                    authorStatement.clearParameters();

                    while (rs.next()) {
                        Author author = new Author(
                                rs.getString("Name"),
                                rs.getDate("dateOfBirth"),
                                rs.getInt("AuthorID"),
                                rs.getString("addedBy"));
                        result.get(i).addAuthor(author);
                    }
                }

                ratingStatement = connection.prepareStatement("SELECT * FROM Rating " +
                        "WHERE isbn = " + result.get(i).getIsbn());
                rs = ratingStatement.executeQuery();
                ratingStatement.clearParameters();
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
        } finally {
            try {
                bookAuthorStatement.close();
                authorStatement.close();
                ratingStatement.close();
                rs.close();
            } catch (SQLException | NullPointerException e) {
                throw new BooksDbException("Couldn't find any matches");
            }
        }
    }
}