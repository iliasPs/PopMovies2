package com.example.android.popmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.example.android.popmovies2.utils.MainViewModel;
import com.example.android.popmovies2.utils.MovieRecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static final String MOVIE_LIST = "instanceMovieList";
    public static final String RECYCLER_VIEW_STATE_KEY = "RECYCLER_VIEW_STATE_KEY";
    public static final String USER_OPTION_KEY = "user_option";
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
    public String userOption = "Most Popular";
    public ArrayList<Movie> mMoviesList = new ArrayList<>();
    private MovieRecycleViewAdapter movieRecycleViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMoviesList != null) {
            outState.putParcelableArrayList(MOVIE_LIST, mMoviesList);
            mListState = mLayoutManager.onSaveInstanceState();
            outState.putParcelable(RECYCLER_VIEW_STATE_KEY, mListState);
            outState.putInt(USER_OPTION_KEY, prefSpinner.getSelectedItemPosition());
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mToolBar = findViewById(R.id.toolbar);

        mToolBar.setTitle(getResources().getString(R.string.app_name));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRV.setLayoutManager(gridLayoutManager);
        mMoviesRV.setHasFixedSize(true);
        movieRecycleViewAdapter = new MovieRecycleViewAdapter(MainActivity.this, mMoviesList);
        mMoviesRV.setAdapter(movieRecycleViewAdapter);

        mLayoutManager = mMoviesRV.getLayoutManager();

        setSpinner();

        prefSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userOption = prefSpinner.getSelectedItem().toString();

                if (userOption.contentEquals("Most Popular") || userOption.contentEquals("Highest Rated")) {
                    PopulateMoviesTask newTask = new PopulateMoviesTask(MainActivity.this);
                    newTask.execute();
                } else {
                    setUpViewModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            ArrayList<Movie> savedMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            Log.d(LOG_TAG, "getting movies from instance ");
            mMoviesList.addAll(savedMovieList);
            movieRecycleViewAdapter = new MovieRecycleViewAdapter(MainActivity.this, mMoviesList);
            mMoviesRV.setAdapter(movieRecycleViewAdapter);
            int spinnerSelection = savedInstanceState.getInt(USER_OPTION_KEY);
            prefSpinner.setSelection(spinnerSelection);
        }else{
            PopulateMoviesTask newTask = new PopulateMoviesTask(this);
            newTask.execute();
            movieRecycleViewAdapter = new MovieRecycleViewAdapter(MainActivity.this, mMoviesList);
            mMoviesRV.setAdapter(movieRecycleViewAdapter);
        }
        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void setSpinner() {
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.pref_spinner_item_list,
                getResources().getStringArray(R.array.userPrefs));

        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefSpinner.setAdapter(spinAdapter);
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mMoviesList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mListState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListState= mMoviesRV.getLayoutManager().onSaveInstanceState();

    }


}
