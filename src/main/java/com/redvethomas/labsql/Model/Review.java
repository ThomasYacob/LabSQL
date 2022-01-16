package com.redvethomas.labsql.Model;

import java.util.Date;

/**
 * Representation of the review object
 *
 * @author Redve & Thomas Yacob
 */
public class Review {

    private int rating;
    private String storyLine;
    private Date reviewDate;
    private User user;

    /**
     * The review constructor
     * @param rating The rating of the review object that is sent to the book
     * @param storyLine The storyLine of the review object that is sent to the book
     * @param reviewDate The reviewDate of the review object that is sent to the book
     * @param user The user that makes the review
     */
    public Review(int rating, String storyLine, Date reviewDate, User user) {
        this.rating = rating;
        this.storyLine = storyLine;
        this.reviewDate = reviewDate;
        this.user = user;
    }

    /**
     * The review constructor without a user
     * @param rating The rating of the review object that is sent to the book
     * @param storyLine The storyLine of the review object that is sent to the book
     * @param reviewDate The reviewDate of the review object that is sent to the book
     */
    public Review(int rating, String storyLine, Date reviewDate) {
        this.rating = rating;
        this.storyLine = storyLine;
        this.reviewDate = reviewDate;
    }

    /**
     * The getter of the rating
     * @return returns rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * The setter of the rating
     * @param rating the rating parameter
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * The getter of the storyline
     * @return returns storyline
     */
    public String getStoryLine() {
        return storyLine;
    }

    /**
     * The setter of the storyline
     * @param storyLine the storyline parameter
     */
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    /**
     * The getter of the reviewDate
     * @return returns reviewDate
     */
    public Date getReviewDate() {
        return reviewDate;
    }

    /**
     * The getter of the user
     * @return returns user
     */
    public User getUser() {
        return user;
    }

    /**
     * The setter of the user
     * @param user returns user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * The toString of the object
     * @return the toString
     */
    @Override
    public String toString() {
        return  "Rating: " + rating + "\n" +
                "Storyline: " + storyLine + "\n" +
                "Reviewdate: " + reviewDate + "\n" +
                "User: " + user;
    }
}
