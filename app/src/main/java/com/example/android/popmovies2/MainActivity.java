package com.example.android.popmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.popmovies2.database.AppDatabase;
import com.example.android.popmovies2.model.Movie;
import com.example.android.popmovies2.model.MovieList;
import com.example.android.popmovies2.utils.JsonUtils;
import com.example.android.popmovies2.utils.MainViewModel;
import com.example.android.popmovies2.utils.MovieRecycleViewAdapter;
import com.example.android.popmovies2.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static final String MOVIE_LIST = "instanceMovieList";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    Context mContext;
    Toolbar mToolBar;
    @BindView(R.id.prefSpinnner)
    Spinner prefSpinner;
    @BindView(R.id.noDataTv)
    TextView noDataTv;
    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRV;
    private AppDatabase mDb;
    private MovieList movieList;
    private String userOption = "Most Popular";
    private List<Movie> mMoviesList;
    private MovieRecycleViewAdapter movieRecycleViewAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mMoviesList !=null) {
            outState.putParcelableArrayList(MOVIE_LIST, (ArrayList<? extends Parcelable>) mMoviesList);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(MOVIE_LIST))
            mMoviesList = savedInstanceState.getParcelable(MOVIE_LIST);

        }

        movieRecycleViewAdapter = new MovieRecycleViewAdapter(MainActivity.this, mMoviesList);
        mMoviesRV.setAdapter(movieRecycleViewAdapter);

        mToolBar = findViewById(R.id.toolbar);


        mToolBar.setTitle(getResources().getString(R.string.app_name));

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.pref_spinner_item_list, getResources().getStringArray(R.array.userPrefs));
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefSpinner.setAdapter(spinAdapter);

        prefSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userOption = prefSpinner.getSelectedItem().toString();

                if (userOption.contentEquals("Most Popular") || userOption.contentEquals("Highest Rated")) {
                    PopulateMoviesTask newTask = new PopulateMoviesTask();
                    newTask.execute();
                } else {
                    setUpViewModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                PopulateMoviesTask newTask = new PopulateMoviesTask();
                newTask.execute();
            }
        });
        mDb = AppDatabase.getInstance(getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRV.setLayoutManager(gridLayoutManager);
        mMoviesRV.setHasFixedSize(true);

    }

    private void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(LOG_TAG, "updating list of movies from livedata in viewmodel");
                movieRecycleViewAdapter.setMovies(movies);
            }
        });
    }



    private class PopulateMoviesTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchMovieObjectUrl = NetworkUtils.createUrl(userOption);
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
                mMoviesRV.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);
            } else {
                mMoviesRV.setVisibility(View.VISIBLE);
                noDataTv.setVisibility(View.GONE);
                mMoviesList = JsonUtils.extractFeatureFromJson(jsonString);
            }

        }
    }

}
