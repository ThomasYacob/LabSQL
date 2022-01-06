package com.redvethomas.labsql.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
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
    private int rating;
    private ArrayList<Author> authors;

    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(String isbn, String title, Genre genre, int rating, Date published, String authorName, String authorId, Date dateOfBirth) {
        if(!isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid isbn");
        }
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.published = published;
        this.authors = new ArrayList<>();
        authors.add(new Author(authorName, dateOfBirth, authorId));
    }

    private static final Pattern isbnPattern = Pattern.compile("^[- ]?[0-9]{13}$");
    public static boolean isValidIsbn(String isbn) {
        return isbnPattern.matcher(isbn).matches();
    }

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

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
