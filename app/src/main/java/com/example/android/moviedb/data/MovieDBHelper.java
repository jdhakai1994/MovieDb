package com.example.android.moviedb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviedb.data.MovieContract.*;

/**
 * Created by Jayabrata Dhakai on 4/3/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 4;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVOURITE_TABLE =
                "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                        FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        FavouriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        FavouriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        FavouriteEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                        FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavouriteEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                        FavouriteEntry.COLUMN_BACKDROP + " BLOB NOT NULL, " +

                        " UNIQUE (" + FavouriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
