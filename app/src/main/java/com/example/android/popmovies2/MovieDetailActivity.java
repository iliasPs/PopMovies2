package com.example.android.popmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.popmovies2.database.AppDatabase;
import com.example.android.popmovies2.model.Movie;
import com.example.android.popmovies2.model.Review;
import com.example.android.popmovies2.utils.AppExecutors;
import com.example.android.popmovies2.utils.JsonUtils;
import com.example.android.popmovies2.utils.MovieReviewsRecyclerViewAdapter;
import com.example.android.popmovies2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity  {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    @BindView(R.id.movieTitle)
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
    @BindView(R.id.fakeView)
    View fakeView;
    @BindView(R.id.reviewLabel)
    TextView reviewLabel;
    @BindView(R.id.fakeView2)
    View fakeView2;
    @BindView(R.id.addFavorite)
    RadioButton addToFavsRB;
    @BindView(R.id.removeFavorite)
    RadioButton removeFavsRB;
    @BindView(R.id.favoritesRadioGroup)
    RadioGroup favoritesRG;
    private List<Review> mReviews;
    private MovieReviewsRecyclerViewAdapter mAdapter;
    private AppDatabase mDb;
    Movie mMovie;



    public static final String EXTRA_MOVIE = "MOVIE";
    public static final String INSTANCE_MOVIE_ID = "instanceMovieId";
    private static final int DEFAULT_MOVIE_ID = -1;
    private int mMovieId = DEFAULT_MOVIE_ID;



    private boolean isinFavsAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Log.d(LOG_TAG, "activity created again ");

        mDb = AppDatabase.getInstance(getApplicationContext());

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.movie_details_title);
        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }

        Intent i = getIntent();

        if (i !=null && i.hasExtra(EXTRA_MOVIE)) {
            if (mMovieId == DEFAULT_MOVIE_ID){
                mMovieId = i.getIntExtra(EXTRA_MOVIE, DEFAULT_MOVIE_ID);
            final LiveData<Movie> movieData = mDb.movieDao().loadMovieById(mMovieId);
                Log.d(LOG_TAG, "movie received " + movieData);
                mMovie = i.getParcelableExtra(EXTRA_MOVIE);
//ASDASDF//
//            Movie movieViewedInDetails = movieData.getValue();

//                mMovie = new Movie( movieViewedInDetails.getTitle(), movieViewedInDetails.getReleaseDate(),
//                        movieViewedInDetails.getMoviePoster(), movieViewedInDetails.getAvgVote(), movieViewedInDetails.getPlot(), movieViewedInDetails.getMovieId());

            movieData.observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movie) {
                    movieData.removeObserver(this);
                    Log.d(LOG_TAG, "Receiving database update from LiveData");
                    populateUI(movie);
                    isMovieInFavorites(movie.getId());

                }
            });
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReviewList.setLayoutManager(linearLayoutManager);


        favoritesButtons(favoritesRG);
        PopulateReviewsAndVideosTask task = new PopulateReviewsAndVideosTask();
        task.execute();
    }

    private void populateUI(Movie movie) {
        Picasso.with(this)
                .load(movie.getMoviePoster())
                .into(moviePosterIv);

        movieTitleTv.setText(movie.getTitle());
        movieReleaseDateTv.setText(movie.getReleaseDate());
        movieAvgTv.setText(movie.getAvgVote());
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
        LiveData<Movie> dbMovieID = mDb.movieDao().loadMovieById(id);
        dbMovieID.observe(this, movieID -> {
                    if (movieID != null) {
                        addToFavsRB.setChecked(true);
                        isinFavsAlready = true;
                    } else if(movieID == null) {
                        addToFavsRB.setChecked(false);
                        isinFavsAlready = false;

                    }
                }
        );
        return isinFavsAlready;
    }


    public void favoritesButtons(View v) {
        favoritesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.addFavorite:
                        Log.d(LOG_TAG, "isInFavs" + isinFavsAlready);
                        if (!isinFavsAlready) {
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {

                                    mDb.movieDao().insertMovie(mMovie);
                                }
                            });
                            Log.d(LOG_TAG, "Adding movie in favs " + mMovie.getTitle());
                            Toast.makeText(getApplication().getApplicationContext(), getResources().getString(R.string.addedToFavorites), Toast.LENGTH_LONG).show();
                            break;}
                         else {
                            Toast.makeText(getApplication().getApplicationContext(), getResources().getString(R.string.alreadytoFavs), Toast.LENGTH_LONG).show();
                            break;
                        }
                    case R.id.removeFavorite:
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {

                            @Override
                            public void run() {
                                mDb.movieDao().deleteMovie(mMovie);
                                Log.d(LOG_TAG, "Removing movie from favs " + mMovie.getTitle());

                            }
                        });
                        Toast.makeText(getApplication().getApplicationContext(), getResources().getString(R.string.removeFavorites), Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }




    private class PopulateReviewsAndVideosTask extends AsyncTask<URL, Void, String> {



        @Override
        protected String doInBackground(URL... urls) {
            URL searchReviewUrl = NetworkUtils.createReviewsUrl(mMovie.getMovieId());
            String jsonString = "";
            try {
                jsonString = NetworkUtils.makeHttpRequest(searchReviewUrl);
            } catch (IOException e) {
                Log.e("Main Activity", "Problem making the HTTP request.", e);
            }
            return jsonString;
        }



        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString == null) {
                fakeView.setVisibility(View.VISIBLE);
            }
            mReviews = JsonUtils.extractReviewsFromJson(jsonString);
            if (mReviews.isEmpty()) {
                reviewLabel.setText(R.string.reviewLabelNone);
            } else {
                reviewLabel.setText(R.string.reviewLabelExist);
                fakeView.setVisibility(View.GONE);
                mAdapter = new MovieReviewsRecyclerViewAdapter(MovieDetailActivity.this, mReviews);
                mReviewList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                mReviewList.setAdapter(mAdapter);
                mReviewList.setVisibility(View.GONE);
            }
        }
    }






}