//package com.example.android.popmovies2.utils;
//
//import android.arch.lifecycle.ViewModel;
//import android.arch.lifecycle.ViewModelProvider;
//import android.support.annotation.NonNull;
//
//import com.example.android.popmovies2.database.AppDatabase;
//
//public class AddMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
//
//    private final AppDatabase mdb;
//    private final int mMovieId;
//
//    public AddMovieViewModelFactory(AppDatabase database, int movieId){
//        mdb = database;
//        mMovieId = movieId;
//    }
//
//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        return (T) new AddMovieViewModelFactory(mdb, mMovieId);
//    }
//}
