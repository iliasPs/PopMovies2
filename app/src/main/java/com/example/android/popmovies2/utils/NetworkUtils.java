package com.example.android.popmovies2.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popmovies2.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    final static private String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular?";
    final static private String MOST_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated?";
    final static private String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    final static private String VIDEOS_URL = "videos";
    final static private String REVIEWS_URL = "reviews";
    final static private String API_KEY = BuildConfig.ApiKey;

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String menuOption) {

        String baseUrl = "";
        switch (menuOption){
            case "Most Popular": baseUrl = POPULAR_URL;
            break;
            case "Highest Rated": baseUrl = MOST_RATED_URL;
            break;
        }

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("api_key",API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "url is " + url);
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    public static URL createVideosUrl(String movieID) {

        String baseUrl = BASE_MOVIE_URL;

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(movieID)
                .appendEncodedPath(VIDEOS_URL)
                .appendQueryParameter("api_key",API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    public static URL createReviewsUrl(String movieID) {
        String baseUrl = BASE_MOVIE_URL;
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(movieID)
                .appendEncodedPath(REVIEWS_URL)
                .appendQueryParameter("api_key",API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = JsonUtils.readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

}
