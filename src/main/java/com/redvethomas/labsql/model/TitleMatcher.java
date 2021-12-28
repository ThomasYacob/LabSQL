package com.redvethomas.labsql.model;

public class TitleMatcher implements BookMatcher{

    private String toMatch;

    public TitleMatcher(String toMatch) {
        this.toMatch = toMatch;
    }

    @Override
    public boolean matches(Book bookToMatch) {
        return bookToMatch.getTitle().contains(toMatch);
    }
}
