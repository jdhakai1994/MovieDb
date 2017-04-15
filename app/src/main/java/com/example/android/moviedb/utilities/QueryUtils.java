package com.example.android.moviedb.utilities;

import android.content.Context;
import android.net.Uri;

import com.example.android.moviedb.R;


/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";

    private static final String API_VERSION_PATH = "3";
    private static final String API_TYPE_PATH = "movie";
    private static final String HIGHEST_RATED_PATH = "top_rated";
    private static final String MOST_POPULAR_PATH = "popular";
    private static final String REVIEW_PATH = "reviews";
    private static final String VIDEO_PATH = "videos";

    private static final String PAGE_QUERY_PARAMETER = "page";
    private static final String API_QUERY_PARAMETER = "api_key";

    private static final String API_KEY_CONSTANT = "0714b520af4b472889d9b6ce22e54173";

    private static final String IMAGE_SIZE = "w342";
    private static final String IMAGE_SIZE_DETAIL = "w185";
    private static final String IMAGE_BASE_URI = "https://image.tmdb.org/t/p";

    private static final String YOUTUBE_IMAGE_AUTHORITY = "i.ytimg.com";
    private static final String YOUTUBE_VIEW_AUTHORITY = "www.youtube.com";

    private static final String YOUTUBE_IMAGE_PATH = "vi";
    private static final String YOUTUBE_IMAGE_SIZE_PATH = "hqdefault.jpg";
    private static final String YOUTUBE_VIDEO_PATH = "watch";

    private static final String YOUTUBE_VIDEO_QUERY_PARAMETER = "v";

    /**
     * Helper Method to build a uri based on sort option
     *
     * @param context      is the application context
     * @param sortByOption is the sort option choice most_popular/highest_rated
     * @return the final uri in String format
     */
    public static String getInitialUrl(Context context, String sortByOption) {
        Uri.Builder builder = new Uri.Builder();

        // sample -> https://api.themoviedb.org/3/movie/popular?page=1&api_key=****************
        if (sortByOption.equals(context.getString(R.string.most_popular)))
            builder.scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(API_VERSION_PATH)
                    .appendPath(API_TYPE_PATH)
                    .appendPath(MOST_POPULAR_PATH)
                    .appendQueryParameter(PAGE_QUERY_PARAMETER, "1")
                    .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

            // sample -> https://api.themoviedb.org/3/movie/top_rated?page=1&api_key=****************
        else if (sortByOption.equals(context.getString(R.string.highest_rated)))
            builder.scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(API_VERSION_PATH)
                    .appendPath(API_TYPE_PATH)
                    .appendPath(HIGHEST_RATED_PATH)
                    .appendQueryParameter(PAGE_QUERY_PARAMETER, "1")
                    .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

        return builder.build().toString();
    }

    /**
     * Helper Method to get the poster image URL for the main activity
     * @param posterPath is the path to the image
     * @return the URL in String format
     */
    public static String getPosterImageUrl(String posterPath) {

        Uri uri = Uri.parse(IMAGE_BASE_URI);
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(IMAGE_SIZE)
                .appendPath(posterPath.substring(1));

        return builder.build().toString();
    }

    /**
     * Helper Method to get the poster image URL for the detail activity/fragment
     * @param posterPath is the path to the image
     * @return the URL in String format
     */
    public static String getPosterImageUrlDetail(String posterPath) {

        Uri uri = Uri.parse(IMAGE_BASE_URI);
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(IMAGE_SIZE_DETAIL)
                .appendPath(posterPath.substring(1));

        return builder.build().toString();
    }

    /**
     * Helper Method to get the backdrop image URL
     * @param backdropPath is the path to the image
     * @return the URL in String format
     */
    public static String getBackdropImageUrl(String backdropPath) {

        Uri uri = Uri.parse(IMAGE_BASE_URI);
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(IMAGE_SIZE)
                .appendPath(backdropPath.substring(1));

        return builder.build().toString();
    }

    /**
     * Helper Method to build a url to fetch review based on movie id
     *
     * @param context is the application context
     * @param id is the movie id
     * @return the final uri in String format
     */
    public static String getReviewUrl(Context context, Integer id) {
        Uri.Builder builder = new Uri.Builder();

        // sample -> https://api.themoviedb.org/3/movie/278/reviews&api_key=****************
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION_PATH)
                .appendPath(API_TYPE_PATH)
                .appendPath(id.toString())
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

        return builder.build().toString();
    }

    /**
     * Helper Method to build a url to fetch video based on movie id
     *
     * @param context is the application context
     * @param id is the movie id
     * @return the final uri in String format
     */
    public static String getTrailerUrl(Context context, Integer id) {
        Uri.Builder builder = new Uri.Builder();

        // sample -> https://api.themoviedb.org/3/movie/278/videos&api_key=****************
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION_PATH)
                .appendPath(API_TYPE_PATH)
                .appendPath(id.toString())
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(API_QUERY_PARAMETER, API_KEY_CONSTANT);

        return builder.build().toString();
    }

    /**
     * Helper Method to build a url to fetch video thumbnail based on movie id
     *
     * @param key is the unique key representing resource in YouTube API
     * @return the final uri in String format
     */
    public static String getVideoImageUrl(String key) {

        Uri.Builder builder = new Uri.Builder();

        // sample -> https://img.youtube.com/vi/EZ-zFwuR0FY/default.jpg
        builder.scheme(SCHEME)
                .authority(YOUTUBE_IMAGE_AUTHORITY)
                .appendPath(YOUTUBE_IMAGE_PATH)
                .appendPath(key)
                .appendPath(YOUTUBE_IMAGE_SIZE_PATH);

        return builder.build().toString();
    }

    /**
     * Helper Method to get youtube URL to be used to launch the video intent
     * based on the key
     *
     * @param key id the unique key which identify a youtube data
     * @return the url in String format
     */
    public static String getYouTubeUrl(String key) {

        Uri.Builder builder = new Uri.Builder();

        //sample -> https://www.youtube.com/watch?v=EZ-zFwuR0FY
        builder.scheme(SCHEME)
                .authority(YOUTUBE_VIEW_AUTHORITY)
                .appendPath(YOUTUBE_VIDEO_PATH)
                .appendQueryParameter(YOUTUBE_VIDEO_QUERY_PARAMETER, key);

        return builder.build().toString();
    }
}
