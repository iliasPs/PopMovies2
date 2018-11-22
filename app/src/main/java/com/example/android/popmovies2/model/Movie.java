package com.example.android.popmovies2.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "moviesTable")
public class Movie implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private int id;
    private  String title;
    private  String releaseDate;
    private  String moviePoster;
    private  String avgVote;
    private  String plot;
    private  String movieId;

    @Ignore
    public Movie(String title, String releaseDate, String moviePoster, String avgVote, String plot, String movieId) {

        this.title = title;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.avgVote = avgVote;
        this.plot = plot;
        this.movieId = movieId;

    }

    public Movie(int id, String title, String releaseDate, String moviePoster, String avgVote, String plot, String movieId) {

        this.id =id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.avgVote = avgVote;
        this.plot = plot;
        this.movieId = movieId;

    }

    public int getId() {
        return id;
    }

    public void setId(int mID) {
        this.id = mID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
         this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate){
        this.releaseDate =releaseDate;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster){
        this.moviePoster = moviePoster;
    }

    public String getAvgVote() {
        return avgVote;
    }

    public void setAvgVote(String avgVote){
        this.avgVote = avgVote;
    }


    public String getPlot() {
        return plot;
    }

   public void setPlot(String plot){
        this.plot = plot;
   }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        moviePoster = in.readString();
        avgVote = in.readString();
        plot = in.readString();
        movieId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(moviePoster);
        dest.writeString(avgVote);
        dest.writeString(plot);
        dest.writeString(movieId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}