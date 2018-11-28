package com.example.android.popmovies2;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.popmovies2.utils.JsonUtils;
import com.example.android.popmovies2.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

class PopulateMoviesTask extends AsyncTask<URL, Void, String> {

    private MainActivity mainActivity;

    private static final String LOG_TAG = PopulateMoviesTask.class.getSimpleName();

    public PopulateMoviesTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(URL... urls) {
        Log.d(LOG_TAG, "async called ");
        URL searchMovieObjectUrl = NetworkUtils.createUrl(mainActivity.userOption);
        String jsonString = "";

        try {
            jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
        } catch (IOException e) {
            Log.e("Main Activity", "Problem making the HTTP request.", e);
        }
        return jsonString;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null) {
            mainActivity.mMoviesRV.setVisibility(View.GONE);
            mainActivity.noDataTv.setVisibility(View.VISIBLE);
        } else {
            mainActivity.mMoviesRV.setVisibility(View.VISIBLE);
            mainActivity.noDataTv.setVisibility(View.GONE);
            mainActivity.mMoviesList = JsonUtils.extractFeatureFromJson(jsonString);
        }

    }
}
