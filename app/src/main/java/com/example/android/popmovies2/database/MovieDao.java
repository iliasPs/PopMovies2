package com.example.android.popmovies2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popmovies2.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviesTable ORDER BY id")
    LiveData<List<Movie>> loadAllMovies();

    @Query("SELECT id FROM moviesTable WHERE id = :id")
    LiveData<Integer> searchForFavorite(int id);

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);


}
