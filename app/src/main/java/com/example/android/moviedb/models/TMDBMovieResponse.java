package com.example.android.moviedb.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class TMDBMovieResponse {

    private List<Results> results;

    public TMDBMovieResponse() {
        results = new ArrayList<>();
    }

    public List<Results> getResults() {
        return results;
    }
}
