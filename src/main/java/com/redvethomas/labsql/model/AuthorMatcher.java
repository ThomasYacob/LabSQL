package com.redvethomas.labsql.model;

import java.util.ArrayList;
import java.util.List;

public class AuthorMatcher implements BookMatcher {

    private String toMatch;

    public AuthorMatcher(String toMatch) {
        this.toMatch = toMatch;
    }

    @Override
    public boolean matches(Book bookToMatch) {
        List<Author> tmp = (ArrayList<Author>) bookToMatch.getAuthors();

        for(int i = 0; i < bookToMatch.getAuthors().size(); i++) {
            return tmp.get(i).getName().contains(toMatch);
        }
        return false;
    }
}
