package com.example.android.moviedb.utilities;

import android.content.Context;
import android.net.Uri;

import com.example.android.moviedb.R;


/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";

    private static final String API_VERSION_PATH = "3";
    private static final String API_TYPE_PATH = "movie";
    private static final String HIGHEST_RATED_PATH  = "top_rated";
    private static final String MOST_POPULAR_PATH = "popular";

    private static final String PAGE_QUERY_PARAMETER  = "page";
    private static final String API_QUERY_PARAMETER = "api_key";

    private static final String API_KEY_CONSTANT = "0714b520af4b472889d9b6ce22e54173";

    /**
     * Helper Method to build a uri based on sort option
     * @param context is the application context
     * @param sortByOption is the sort option choice most_popular/highest_rated
     * @return the final uri in String format
     */
    public static String getUriString(Context context, String sortByOption){
        Uri.Builder builder = new Uri.Builder();

        // sample -> https://api.themoviedb.org/3/movie/popular?page=1&api_key=0714b520af4b472889d9b6ce22e54173
        if(sortByOption.equals(context.getString(R.string.most_popular)))
            builder.scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(API_VERSION_PATH)
                    .appendPath(API_TYPE_PATH)
                    .appendPath(MOST_POPULAR_PATH)
                    .appendQueryParameter(PAGE_QUERY_PARAMETER, "1")
                    .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

       // sample -> https://api.themoviedb.org/3/movie/top_rated?page=1&api_key=0714b520af4b472889d9b6ce22e54173
        else if(sortByOption.equals(context.getString(R.string.highest_rated)))
            builder.scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(API_VERSION_PATH)
                    .appendPath(API_TYPE_PATH)
                    .appendPath(HIGHEST_RATED_PATH)
                    .appendQueryParameter(PAGE_QUERY_PARAMETER, "1")
                    .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

        return builder.build().toString();
    }
}
