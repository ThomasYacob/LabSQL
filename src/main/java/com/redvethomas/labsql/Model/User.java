package com.redvethomas.labsql.Model;

/**
 * Representation of the user
 */
public class User {

    private String username;
    private String password;

    /**
     * The user constructor
     * @param username represents the username of the user
     * @param password represents the password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * The user constructor without a password
     * @param username represents the username of the user
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * The getter for the username
     * @return returns the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * The getter for the password
     * @return returns the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * The setter for the username
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The setter for the password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The toString of the object
     * @return returns the toString
     */
    @Override
    public String toString() {
        return  username;
    }
}
