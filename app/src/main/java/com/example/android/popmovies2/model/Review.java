package com.example.android.popmovies2.model;

public class Review {

        private String reviewAuthor;
        private String reviewContent;
        private String reviewID;
        private String reviewURL;

        public Review(String author, String content, String id, String url){

            reviewAuthor= author;
            reviewContent = content;
            reviewID = id;
            reviewURL = url;
        }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewID() {
        return reviewID;
    }

    public String getReviewURL() {
        return reviewURL;
    }
}
