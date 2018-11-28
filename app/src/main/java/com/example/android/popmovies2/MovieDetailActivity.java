package com.example.android.popmovies2;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
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
import com.example.android.popmovies2.utils.MovieReviewsRecyclerViewAdapter;
import com.example.android.popmovies2.utils.MovieTrailersRecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "MOVIE";
    private static final String REVIEW_BUTTON = "reviews";
    private static final String REVIEWS_KEY = "reviews_key";
    private static final String TRAILERS_KEY = "trailers_key";
    private static final String IS_IN_FAVORITES = "isInFavorites";
    private static final String INSTANCE_MOVIE_ID = "instanceMovieId";
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final int DEFAULT_MOVIE_ID = -1;
    public ArrayList<Review> mReviews = new ArrayList<>();
    public ArrayList<Trailer> mTrailers = new ArrayList<>();
    public boolean isReviewButtonClicked;
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
    @BindView(R.id.reviewLabel)
    ToggleButton reviewSwitch;
    @Nullable
    @BindView(R.id.rv_movieReviews)
    RecyclerView mReviewList;
    @Nullable
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailersList;
    @BindView(R.id.fakeView)
    View fakeView;
    @BindView(R.id.fakeView2)
    View fakeView2;
    Movie mMovie;
    @BindView(R.id.favoriteToggle)
    ToggleButton favoriteToggle;
    @BindView(R.id.trailersHeader)
    TextView trailersHeader;
    private MovieTrailersRecyclerViewAdapter mTrailerAdapter;
    private MovieReviewsRecyclerViewAdapter mReviewAdapter;
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
        restoreData(savedInstanceState);

        mReviewAdapter = new MovieReviewsRecyclerViewAdapter(MovieDetailActivity.this, mReviews);
        mReviewList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mReviewList.setAdapter(mReviewAdapter);

        mTrailerAdapter = new MovieTrailersRecyclerViewAdapter(MovieDetailActivity.this, mTrailers);
        mTrailersList.setAdapter(mTrailerAdapter);

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

        if (isReviewButtonClicked) {
            showReviews();
        }
        int movieID = Integer.parseInt(mMovie.getMovieId());
        isMovieInFavorites(movieID);
        reviewSwitch.setOnCheckedChangeListener(new ShowReviewsListener());
        favoriteToggle.setOnCheckedChangeListener(new FavoriteListener());
    }


    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(IS_IN_FAVORITES)) {
            isInFavsAlready = savedInstanceState.getBoolean(IS_IN_FAVORITES, false);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEW_BUTTON)) {
            isReviewButtonClicked = savedInstanceState.getBoolean(REVIEW_BUTTON, false);
            if (isReviewButtonClicked) {
                Log.d(LOG_TAG, "review button after rotation " + isReviewButtonClicked);
                reviewSwitch.setChecked(true);

            }
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEWS_KEY)) {
            mReviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
            mReviewAdapter = new MovieReviewsRecyclerViewAdapter(MovieDetailActivity.this, mReviews);
            mReviewList.swapAdapter(mReviewAdapter, true);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TRAILERS_KEY)) {
            mTrailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
            mTrailerAdapter = new MovieTrailersRecyclerViewAdapter(MovieDetailActivity.this, mTrailers);
            mTrailersList.swapAdapter(mTrailerAdapter, true);
        }

    }


    private void setReviews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReviewList.setLayoutManager(linearLayoutManager);
        FetchReviewsAndTrailersTask task = new FetchReviewsAndTrailersTask(this);
        task.execute();
    }


    private void setTrailers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersList.setLayoutManager(linearLayoutManager);
        FetchReviewsAndTrailersTask task = new FetchReviewsAndTrailersTask(this);
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
        outState.putBoolean(IS_IN_FAVORITES, isInFavsAlready);
        outState.putBoolean(REVIEW_BUTTON, isReviewButtonClicked);
        outState.putParcelableArrayList(REVIEWS_KEY, mReviews);
        outState.putParcelableArrayList(TRAILERS_KEY, mTrailers);
        super.onSaveInstanceState(outState);
    }


    public void showReviews() {
        mReviewList.setHasFixedSize(true);
        mReviewList.setVisibility(View.VISIBLE);
        fakeView2.setVisibility(View.VISIBLE);
        reviewSwitch.setText(R.string.reviewLabelClose);
    }

    private boolean isMovieInFavorites(int id) {
        LiveData<Integer> dbMovieID = mDb.movieDao().searchFavsByMovieID(id);
        dbMovieID.observe(this, movieID -> {
                    if (movieID != null) {
                        Log.d(LOG_TAG, "is in favs " + movieID);
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

    public void populateReviewsAndTrailers(ArrayList<Review> review, ArrayList<Trailer> trailers) {
        if (review.isEmpty()) {
            reviewSwitch.setText(R.string.reviewLabelNone);
        } else {
            reviewSwitch.setText(R.string.reviewLabelExist);
            fakeView.setVisibility(View.GONE);
            mReviewAdapter.swapData(review);
            mReviewList.setVisibility(View.GONE);
        }
        if (trailers.isEmpty()) {
            trailersHeader.setText(R.string.trailersNA);
        } else {
            mTrailerAdapter.swapData(trailers);
            trailersHeader.setText(R.string.trailerHeader);

        }
    }

    private class FavoriteListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked && isInFavsAlready == false) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    mDb.movieDao().insertMovie(mMovie);
                    Log.d(LOG_TAG, "Adding movie in favs " + mMovie.getTitle());
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

    private class ShowReviewsListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                setReviews();
                showReviews();
                reviewSwitch.setText(R.string.reviewLabelClose);
                isReviewButtonClicked = true;
                Log.d(LOG_TAG, "review button " + isReviewButtonClicked);
            } else {
                isReviewButtonClicked = false;
                reviewSwitch.setVisibility(View.VISIBLE);
                if(mReviews!=null){ reviewSwitch.setText(R.string.reviewLabelExist);}
                mReviewList.setVisibility(View.GONE);
                fakeView2.setVisibility(View.GONE);
                fakeView.setVisibility(View.VISIBLE);

            }
        }
    }
}