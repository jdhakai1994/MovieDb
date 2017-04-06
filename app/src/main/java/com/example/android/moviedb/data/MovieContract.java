package com.example.android.moviedb.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jayabrata Dhakai on 4/3/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.moviedb";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE = "favourite";

    public static final class FavouriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE)
                .build();

        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
    }
}
