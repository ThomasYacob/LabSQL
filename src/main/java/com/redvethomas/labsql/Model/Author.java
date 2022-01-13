package com.redvethomas.labsql.Model;

import java.util.Date;

public class Author {

    private String authorName;
    private final Date dateOfBirth;
    private int authorId;

    /**
     * This is the constructor for author
     * @param authorName This is the authors name
     * @param dateOfBirth This is the authors date of birth
     * @param authorId This is the authorId which is unique
     */
    public Author(String authorName, Date dateOfBirth, int authorId) {
        this.authorName = authorName;
        this.dateOfBirth = dateOfBirth;
        this.authorId = authorId;
    }
    /**
     * This is the constructor for author which has not a given authorId
     * @param authorName This is the authors name
     * @param dateOfBirth This is the authors date of birth
     */
    public Author(String authorName, Date dateOfBirth) {
        this(authorName, dateOfBirth, -1);
    }
    /**
     * This is a getter for the authors name
     * @return returns the authors name
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * This is a setter for the authors name
     * @param authorName The name that wants to be set
     */
    public void setName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * This is a getter for the authors date of birth
     * @return returns the authors date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * This is a getter for the authors Id
     * @return returns the author Id
     */
    public int getAuthorID() {
        return authorId;
    }

    /**
     * This is a setter for the authors Id
     * @param authorId The name author Id that wants to be set
     */
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    /**
     * This is toString for author
     * @return returns the information for the author object
     */
    @Override
    public String toString() {
        return "Author{" +
                "name='" + authorName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
