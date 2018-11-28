package com.example.android.popmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

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

    protected Review(Parcel in) {
        reviewAuthor = in.readString();
        reviewContent = in.readString();
        reviewID = in.readString();
        reviewURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
        dest.writeString(reviewID);
        dest.writeString(reviewURL);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}