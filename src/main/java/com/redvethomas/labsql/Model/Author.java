package com.redvethomas.labsql.Model;

import java.time.LocalDate;
import java.util.Date;

public class Author {

    private String name;
    private Date dateOfBirth;
    private String authorId;

    public Author(String name, Date dateOfBirth, String authorId) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAuthorID() {
        return authorId;
    }



    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
