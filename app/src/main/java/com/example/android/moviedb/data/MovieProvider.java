package com.example.android.moviedb.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jayabrata Dhakai on 4/3/2017.
 */

public class MovieProvider extends ContentProvider {
    private MovieDBHelper mOpenHelper;

    public static final int CODE_FAVOURITE = 100;
    public static final int CODE_FAVOURITE_WITH_MOVIE_ID = 101;

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITE, CODE_FAVOURITE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITE + "/#", CODE_FAVOURITE_WITH_MOVIE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE:
                cursor = db.query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVOURITE_WITH_MOVIE_ID:
                selection = MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = db.query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in MovieDb.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE:
                Log.d(LOG_TAG, String.valueOf(CODE_FAVOURITE));
                rowId = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, values);
                break;
            case CODE_FAVOURITE_WITH_MOVIE_ID:
                Log.d(LOG_TAG, String.valueOf(CODE_FAVOURITE_WITH_MOVIE_ID));
                throw new IllegalArgumentException("Unknown uri: " + uri);
            default:
                Log.d(LOG_TAG, "Default");
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        if (rowId != -1)
            return Uri.withAppendedPath(MovieContract.FavouriteEntry.CONTENT_URI, String.valueOf(rowId));
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int noOfRowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE:
                throw new IllegalArgumentException("Unknown uri: " + uri);
            case CODE_FAVOURITE_WITH_MOVIE_ID:
                selection = MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                noOfRowsDeleted = db.delete(MovieContract.FavouriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        if (noOfRowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return noOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in MovieDb");
    }
}