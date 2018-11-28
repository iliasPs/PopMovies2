package com.example.android.popmovies2.utils;

import android.arch.persistence.room.TypeConverter;

import com.example.android.popmovies2.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {

        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<Movie> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }

