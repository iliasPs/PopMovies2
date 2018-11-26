package com.example.android.popmovies2;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.example.android.popmovies2.database.AppDatabase;
import com.example.android.popmovies2.model.Movie;
import com.example.android.popmovies2.model.Review;
import com.example.android.popmovies2.model.Trailer;
import com.example.android.popmovies2.utils.AppExecutors;
import com.example.android.popmovies2.utils.JsonUtils;
import com.example.android.popmovies2.utils.MovieReviewsRecyclerViewAdapter;
import com.example.android.popmovies2.utils.MovieTrailersRecyclerViewAdapter;
import com.example.android.popmovies2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "MOVIE";
    public static final String INSTANCE_MOVIE_ID = "instanceMovieId";
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final int DEFAULT_MOVIE_ID = -1;
    @BindView(R.id.movieTitleDetails)
    TextView movieTitleTv;
    @BindView(R.id.movieReleaseDate)
    TextView movieReleaseDateTv;
    @BindView(R.id.movieAvg)
    TextView movieAvgTv;
    @BindView(R.id.moviePlot)
    TextView moviePlotTv;
    @BindView(R.id.moviePoster)
    ImageView moviePosterIv;
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.rv_movieReviews)
    RecyclerView mReviewList;
    @Nullable
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailersList;
    @BindView(R.id.fakeView)
    View fakeView;
    @BindView(R.id.reviewLabel)
    TextView reviewLabel;
    @BindView(R.id.fakeView2)
    View fakeView2;
    Movie mMovie;
    @BindView(R.id.favoriteToggle)
    ToggleButton favoriteToggle;
    @BindView(R.id.trailersHeader)
    TextView trailersHeader;
    private List<Review> mReviews;
    private List<Trailer> mTrailers;
    private MovieTrailersRecyclerViewAdapter mTrailerAdapter;
    private MovieReviewsRecyclerViewAdapter mAdapter;
    private AppDatabase mDb;
    private int mMovieId = DEFAULT_MOVIE_ID;


    private boolean isInFavsAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mDb = AppDatabase.getInstance(getApplicationContext());
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.movie_details_title);
        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }

        Intent i = getIntent();

        if (i != null && i.hasExtra(EXTRA_MOVIE)) {
            if (mMovieId == DEFAULT_MOVIE_ID) {
                mMovieId = i.getIntExtra(EXTRA_MOVIE, DEFAULT_MOVIE_ID);
                mMovie = i.getParcelableExtra(EXTRA_MOVIE);
                populateUI(mMovie);

            }
        }

        setTrailers();
        setReviews();
        int movieID = Integer.parseInt(mMovie.getMovieId());
        isMovieInFavorites(movieID);
        favoriteToggle.setOnCheckedChangeListener(new FavoriteListener());

    }

    private void setReviews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReviewList.setLayoutManager(linearLayoutManager);
        FetchReviewsAndTrailersTask task = new FetchReviewsAndTrailersTask();
        task.execute();
    }

    private void setTrailers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersList.setLayoutManager(linearLayoutManager);
        FetchReviewsAndTrailersTask task = new FetchReviewsAndTrailersTask();
        task.execute();
    }

    private void populateUI(Movie movie) {
        Picasso.get()
                .load(movie.getMoviePoster())
                .into(moviePosterIv);

        movieTitleTv.setText(movie.getTitle());
        movieReleaseDateTv.setText(movie.getReleaseDate());
        movieAvgTv.setText(movie.getAvgVote() + getString(R.string.vote_max));
        moviePlotTv.setText(movie.getPlot());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    public void showReviews(View v) {
        mReviewList.setHasFixedSize(true);
        mReviewList.setVisibility(View.VISIBLE);
        fakeView2.setVisibility(View.VISIBLE);
    }

    public boolean isMovieInFavorites(int id) {
        LiveData<Integer> dbMovieID = mDb.movieDao().searchFavsByMovieID(id);
        dbMovieID.observe(this, movieID -> {
                     if (movieID != null) {
                        isInFavsAlready = true;
                        favoriteToggle.setChecked(true);
                        favoriteToggle.setText(getString(R.string.addedToFavorites));
                    } else if (movieID == null) {
                        isInFavsAlready = false;
                        favoriteToggle.setChecked(false);
                        favoriteToggle.setText(getString(R.string.addFavorite));
                    }
                }
        );
        return isInFavsAlready;

    }

    private class FavoriteListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked == true && isInFavsAlready == false) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().insertMovie(mMovie);
                        Log.d(LOG_TAG, "Adding movie in favs " + mMovie.getTitle());
                    }
                });
            } else if (isChecked == false) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = Integer.parseInt(mMovie.getMovieId());
                        mDb.movieDao().deleteByID(id);
                        Log.d(LOG_TAG, "Removing movie from favs " + mMovie.getTitle());
                    }
                });
            }

        }
    }


    private class FetchReviewsAndTrailersTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected String[] doInBackground(URL... urls) {
            URL searchReviewUrl = NetworkUtils.createReviewsUrl(mMovie.getMovieId());
            URL searchVideoUrl = NetworkUtils.createVideosUrl(mMovie.getMovieId());
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
                fakeView.setVisibility(View.VISIBLE);
            }
            mTrailers = JsonUtils.extractTrailersFromJson(jsonString[0]);
            mReviews = JsonUtils.extractReviewsFromJson(jsonString[1]);
            populateReviews(mReviews, mTrailers);
        }
    }


    private void populateTrailers(List<Trailer> trailers){
        if(trailers.isEmpty()){
            return;
        }
        else{

        }
    }

    private void populateReviews(List<Review> review, List<Trailer> trailers){

        if (review.isEmpty()) {
            reviewLabel.setText(R.string.reviewLabelNone);
        } else {
            reviewLabel.setText(R.string.reviewLabelExist);
            fakeView.setVisibility(View.GONE);
            mAdapter = new MovieReviewsRecyclerViewAdapter(MovieDetailActivity.this, mReviews);
            mReviewList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            mReviewList.setAdapter(mAdapter);
            mReviewList.setVisibility(View.GONE);
        }

        if(trailers.isEmpty()){
            trailersHeader.setText(R.string.trailersNA);
        }else{
            trailersHeader.setText(R.string.trailerHeader);
            mTrailerAdapter = new MovieTrailersRecyclerViewAdapter(MovieDetailActivity.this, mTrailers);
            mTrailersList.setAdapter(mTrailerAdapter);
        }
    }

}