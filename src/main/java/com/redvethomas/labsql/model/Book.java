package com.redvethomas.labsql.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {

    private int bookId;
    private String isbn; // should check format
    private String title;
    private Date published;
    private String storyLine = "";
    private Genre genre;
    private int rating;
    private ArrayList<Author> authors;
    // TODO: 
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(int bookId, String isbn, String title, Date published, Genre genre, int rating) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating = rating;
        this.authors = new ArrayList<>();
    }
    
    public Book(String isbn, String title, Date published, Genre genre, int rating) {
        this(-1, isbn, title, published, genre, rating);
    }

    private static final String isbnPattern = "^[- ]?[0-9]{13}";

    public void addAuthor(Author author) {
        authors.add(author);
    }

    public boolean removeAuthor(Author author) {
        return authors.remove(author);
    }

    public ArrayList<Author> getAuthors() {
        return (ArrayList<Author>) authors.clone();
    }
    public Genre getGenre() {
        return genre;
    }
    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    public String getStoryLine() { return storyLine; }
    public int getRating() {
        return rating;
    }

    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }
    
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
