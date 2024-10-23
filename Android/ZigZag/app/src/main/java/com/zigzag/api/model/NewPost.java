package com.zigzag.api.model;

/* This may seem redundant, but since new posts have different fields than already created post,
 * a different class needs to be used.
 */
public class NewPost {
    private String text;
    private String author;
    private String expiryDate;
    private double postLatitude;
    private double postLongitude;

    public NewPost(String text, String author, String expiryDate, double postLatitude, double postLongitude) {
        this.text = text;
        this.author = author;
        this.expiryDate = expiryDate;
        this.postLatitude = postLatitude;
        this.postLongitude = postLongitude;
    }
}
