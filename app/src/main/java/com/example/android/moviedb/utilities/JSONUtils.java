package com.example.android.moviedb.utilities;

import com.example.android.moviedb.models.MovieListResponse;
import com.example.android.moviedb.models.MovieReviewResponse;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.models.Review;
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
     * @param moviewListJsonResponse is the JSON response as one complete String
     * @return the parsed JSON as a ArrayList
     */
    public static List<Result> parseMovieListJSON(String moviewListJsonResponse) {
        Gson gson = new GsonBuilder().create();
        if(moviewListJsonResponse == null)
            return null;
        MovieListResponse movieListResponse = gson.fromJson(moviewListJsonResponse, MovieListResponse.class);
        return movieListResponse.getResults();
    }

    /**
     * Helper Method to parse the JSON response
     * @param reviewJsonResponse is the JSON response as one complete String
     * @return the parsed JSON as a ArrayList
     */
    public static List<Review> parseReviewJSON(String reviewJsonResponse) {
        Gson gson = new GsonBuilder().create();
        if(reviewJsonResponse == null)
            return null;
        MovieReviewResponse reviewListResponse = gson.fromJson(reviewJsonResponse, MovieReviewResponse.class);
        return reviewListResponse.getReviews();
    }
}
