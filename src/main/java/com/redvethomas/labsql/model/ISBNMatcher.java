package com.redvethomas.labsql.model;

public class ISBNMatcher implements BookMatcher {

    private String toMatch;

    public ISBNMatcher(String toMatch) {
        this.toMatch = toMatch;
    }

    @Override
    public boolean matches(Book bookToMatch) {
        return toMatch.contains(bookToMatch.getIsbn());
    }
}
