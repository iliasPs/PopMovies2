package com.example.android.popmovies2;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.popmovies2.utils.JsonUtils;
import com.example.android.popmovies2.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

class FetchReviewsAndTrailersTask extends AsyncTask<URL, Void, String[]> {

    private MovieDetailActivity movieDetailActivity;

    public FetchReviewsAndTrailersTask(MovieDetailActivity movieDetailActivity) {
        this.movieDetailActivity = movieDetailActivity;
    }

    @Override
    protected String[] doInBackground(URL... urls) {
        URL searchReviewUrl = NetworkUtils.createReviewsUrl(movieDetailActivity.mMovie.getMovieId());
        URL searchVideoUrl = NetworkUtils.createVideosUrl(movieDetailActivity.mMovie.getMovieId());
        String jsonReviewString = "";
        String jsonVideoString = "";
        try {
            jsonReviewString = NetworkUtils.makeHttpRequest(searchReviewUrl);
            jsonVideoString = NetworkUtils.makeHttpRequest(searchVideoUrl);
        } catch (IOException e) {
            Log.e("Main Activity", "Problem making the HTTP request.", e);
        }
        return new String[]{jsonVideoString, jsonReviewString};
    }
    @Override
    protected void onPostExecute(String[] jsonString) {
        if (jsonString == null) {
            movieDetailActivity.fakeView.setVisibility(View.VISIBLE);
        }
        movieDetailActivity.mTrailers = JsonUtils.extractTrailersFromJson(jsonString[0]);
        movieDetailActivity.mReviews = JsonUtils.extractReviewsFromJson(jsonString[1]);
        movieDetailActivity.populateReviewsAndTrailers(movieDetailActivity.mReviews, movieDetailActivity.mTrailers);
        if(movieDetailActivity.isReviewButtonClicked){
            movieDetailActivity.showReviews();
            movieDetailActivity.reviewSwitch.setText(R.string.reviewLabelClose);
        }

    }
}
