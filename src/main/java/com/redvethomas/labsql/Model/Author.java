package com.redvethomas.labsql.Model;

import java.util.Date;

public class Author {

    private String authorName;
    private final Date dateOfBirth;
    private final String authorId;

    public Author(String authorName, Date dateOfBirth, String authorId) {
        this.authorName = authorName;
        this.dateOfBirth = dateOfBirth;
        this.authorId = authorId;
    }

    public String getName() {
        return authorName;
    }

    public void setName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAuthorID() {
        return authorId;
    }



    @Override
    public String toString() {
        return "Author{" +
                "name='" + authorName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
