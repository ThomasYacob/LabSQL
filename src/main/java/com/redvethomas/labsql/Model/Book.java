package com.redvethomas.labsql.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Representation of a book.
 * 
 * @author Redve Ahmed & Thomas Yacob
 */
public class Book {

    public enum Genre {LITERARY_FICTION, MYSTERY, THRILLER,
        HORROR, HISTORICAL, ROMANCE, WESTERN,
        SPECULATIVE_FICTION, SCIENCE_FICTION,
        FANTASY, DYSTOPIAN, MAGICAL_REALISM, REALIST_LITERATURE}

    private final String isbn; // should check format
    private String title;
    private final Date published;
    private String storyLine = "";
    private Genre genre;
    private ArrayList<Author> authors;
    private ArrayList<Review> reviews;
    private User user;

    /**
     * This is a constructor for the book object
     * @param isbn Represents the isbn of the book
     * @param title Represents the title of the book
     * @param genre Represents the genre of the book
     * @param published Represents when the book was published
     * @param authorName Represents the name of the author who wrote the book
     * @param authorId Represents the id of the author who wrote the book
     * @param dateOfBirth Represents the date of birth of the author
     */
    public Book(String isbn, String title, Genre genre, Date published, String authorName, int authorId, Date dateOfBirth, String username) {
        if(!isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid isbn");
        }
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.published = published;
        this.authors = new ArrayList<>();
        this.reviews = new ArrayList<>();
        authors.add(new Author(authorName, dateOfBirth, authorId, username));
    }

    /**
     * This is a constructor for the book object without the author parameters
     * @param isbn Represents the isbn of the book
     * @param title Represents the title of the book
     * @param genre Represents the genre of the book
     * @param published Represents when the book was published
     */
    public Book(String isbn, String title, Genre genre, Date published) {
        if(!isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid isbn");
        }
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.published = published;
        this.reviews = new ArrayList<>();
        this.authors = new ArrayList<>();
    }

    /**
     * This is a constructor for the book object with a username
     * @param isbn Represents the isbn of the book
     * @param title Represents the title of the book
     * @param genre Represents the genre of the book
     * @param published Represents when the book was published
     * @param username The username for the person who's logged into the database
     */
    public Book(String isbn, String title, Genre genre, Date published, String username) {
        if(!isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid isbn");
        }
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.published = published;
        this.user = new User(username);
        this.reviews = new ArrayList<>();
        this.authors = new ArrayList<>();
    }

    private static final Pattern isbnPattern = Pattern.compile("^[- ]?[0-9]{13}$");

    /**
     * This method checks if the input isbn is valid
     * @param isbn The isbn string
     * @return returns true if it's valid and false if it's not
     */
    public static boolean isValidIsbn(String isbn) {
        return isbnPattern.matcher(isbn).matches();
    }

    /**
     * This method adds a review to the list with reviews
     * @param review the review that is being added
     */
    public void addReview(Review review) {
        reviews.add(review);
    }

    /**
     * This method adds an author to the list with authors
     * @param author the author that is being added
     */
    public void addAuthor(Author author) {
        authors.add(author);
    }

    /**
     * This method removes an author from the list with authors
     * @param author the author that is being removed
     * @return returns the removal of an author
     */
    public boolean removeAuthor(Author author) {
        return authors.remove(author);
    }

    /**
     * This is a getter for the reviews
     * @return returns a cloned list of reviews
     */
    public ArrayList<Review> getReviews() {
        return (ArrayList<Review>) reviews.clone();
    }

    /**
     * This is a getter for the list of authors
     * @return returns a cloned list of the authors
     */
    public ArrayList<Author> getAuthors() {
        return (ArrayList<Author>) authors.clone();
    }

    /**
     * This is a getter for the genre
     * @return returns the genre
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * This is a getter for the isbn
     * @return returns the isbn
     */
    public String getIsbn() { return isbn; }

    /**
     * This is a getter for the title
     * @return returns the title
     */
    public String getTitle() { return title; }

    /**
     * This is a getter for when the book is published
     * @return returns the published date
     */
    public Date getPublished() { return published; }

    /**
     * This is a setter for the user object
     * @param user sets the user object
     */
    public void setUser(User user){
        this.user = user;
    }

    /**
     * This is a getter for the user object
     * @return returns the user object
     */
    public User getUser(){
        return this.user;
    }

    /**
     * This is toString for the book
     * @return returns the information for the book object
     */
    @Override
    public String toString() {
        return isbn + " " + title + ", " + genre + ", " + " " + published.toString()
                + " " + authors;
    }
}
