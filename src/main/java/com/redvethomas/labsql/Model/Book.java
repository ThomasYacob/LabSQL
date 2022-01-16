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
     * @param isbn
     * @param title
     * @param genre
     * @param published
     * @param authorName
     * @param authorId
     * @param dateOfBirth
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
    public static boolean isValidIsbn(String isbn) {
        return isbnPattern.matcher(isbn).matches();
    }

    public void addReview(Review review) {
        reviews.add(review);
    }
    public void addAuthor(Author author) {
        authors.add(author);
    }

    public boolean removeAuthor(Author author) {
        return authors.remove(author);
    }

    public ArrayList<Review> getReviews() {
        return (ArrayList<Review>) reviews.clone();
    }

    public ArrayList<Author> getAuthors() {
        return this.authors;
    }

    public Genre getGenre() {
        return genre;
    }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }

    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }

    @Override
    public String toString() {
        return isbn + " " + title + ", " + genre + ", " + " " + published.toString()
                + " " + authors;
    }
}
