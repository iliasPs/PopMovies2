package com.example.android.popmovies2.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popmovies2.database.AppDatabase;
import com.example.android.popmovies2.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;
    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Actively retrieving movies from db ");
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies(){

        return movies;
    }

}
