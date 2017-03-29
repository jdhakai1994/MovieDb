package com.example.android.moviedb.utilities;

import com.example.android.moviedb.models.Results;
import com.example.android.moviedb.models.TMDBMovieResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class JSONUtils {

    public static final String LOG_TAG = JSONUtils.class.getSimpleName();

    /**
     * Helper Method to parse the JSON response
     * @param response is the JSON response as one complete String
     * @return the parsed JSON as a ArrayList
     */
    public static List<Results> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        if(response == null)
            return null;
        TMDBMovieResponse tmdbMovieResponse = gson.fromJson(response, TMDBMovieResponse.class);
        return tmdbMovieResponse.getResults();
    }
}
