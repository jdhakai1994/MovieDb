package com.example.android.moviedb;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviedb.adapter.MovieAdapter;
import com.example.android.moviedb.data.MovieContract;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class MovieGridActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, MovieAdapter.GridItemClickListener {

    private static final String LOG_TAG = MovieGridActivity.class.getSimpleName();

    private static final int FETCH_MOVIE_FROM_INTERNET_ID = 11;
    private static final int FETCH_MOVIE_FROM_DB_ID = 21;
    private static final int MSG_LOAD_FIRST_ITEM = 61;

    private MovieAdapter mMovieAdapter;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private Context mContext = MovieGridActivity.this;
    private Toast mToast;
    private Boolean mTwoPane = false;

    public static final String mProjection[] = {MovieContract.FavouriteEntry._ID, MovieContract.FavouriteEntry.COLUMN_TITLE, MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE, MovieContract.FavouriteEntry.COLUMN_USER_RATING, MovieContract.FavouriteEntry.COLUMN_SYNOPSIS, MovieContract.FavouriteEntry.COLUMN_POSTER, MovieContract.FavouriteEntry.COLUMN_BACKDROP};

    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_ID = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_USER_RATING = 4;
    public static final int INDEX_MOVIE_SYNOPSIS = 5;
    public static final int INDEX_MOVIE_POSTER = 6;
    public static final int INDEX_MOVIE_BACKDROP = 7;

    private static Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

// Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

// Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

// Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        setContentView(R.layout.activity_movie_grid);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/layout-w720dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mProgressBar = (ProgressBar) findViewById(R.id.pb_main_ui);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view_main_ui);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_main_ui);

        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mLayoutManager = new GridLayoutManager(mContext, 2);
        else
            mLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(mLayoutManager);

        mMovieAdapter = new MovieAdapter(MovieGridActivity.this, this);
        recyclerView.setAdapter(mMovieAdapter);

        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_LOAD_FIRST_ITEM)
                    onClick(mMovieAdapter.getMovieData());
            }
        };

        // if the device is not connected to internet change the text of the empty view
        if (!NetworkUtils.isConnectedToInternet(mContext))
            showNoInternetUI();
        else {
            showFetchingDataUI();
            // Initialise the custom loader
            getSupportLoaderManager().initLoader(FETCH_MOVIE_FROM_INTERNET_ID, loaderBundle, new ResultCallback());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.i_sort_by:
                showMenu(findViewById(R.id.i_sort_by));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Bundle loaderBundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.i_most_popular:
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(mContext, "Fetching Most Popular Movies", Toast.LENGTH_SHORT);
                mToast.show();

                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));
                // if the device is not connected to internet change the text of the empty view
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    showNoInternetUI();
                else {
                    showFetchingDataUI();
                    // Initialise the custom loader
                    getSupportLoaderManager().restartLoader(FETCH_MOVIE_FROM_INTERNET_ID, loaderBundle, new ResultCallback());
                }
                return true;
            case R.id.i_highest_rated:
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(mContext, "Fetching Highest Rated Movies", Toast.LENGTH_SHORT);
                mToast.show();
                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.highest_rated));
                // if the device is not connected to internet change the text of the empty view
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    showNoInternetUI();
                else {
                    showFetchingDataUI();
                    // Initialise the custom loader
                    getSupportLoaderManager().restartLoader(FETCH_MOVIE_FROM_INTERNET_ID, loaderBundle, new ResultCallback());
                }
                return true;
            case R.id.i_favourite:
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(mContext, "Fetching your Favourite Movies", Toast.LENGTH_SHORT);
                mToast.show();
                showFetchingDataUI();
                getSupportLoaderManager().restartLoader(FETCH_MOVIE_FROM_DB_ID, null, new CursorCallback());
                return true;
            default:
                return false;
        }
    }



    /**
     * Helper Method to display the pop-up menu
     *
     * @param v is the view to which the pop-up will be attached
     */
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(MovieGridActivity.this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.sortby_menu);
        popup.show();
    }

    /**
     * Helper Method to display the UI when there is no internet connectivity
     */
    public void showNoInternetUI() {
        mProgressBar.setVisibility(View.GONE);
        mMovieAdapter.setMovieData(null);
        mEmptyView.setText(getString(R.string.no_internet));
    }

    /**
     * Helper Method to display the UI when it is trying to fetch data, especially
     * important in slow internet connection
     */
    public void showFetchingDataUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMovieAdapter.setMovieData(null);
        mEmptyView.setText("");
    }

    /**
     * Inner Class representing the callback from the loader used to setup the
     * main ui section when most_popular/top_rated option is selected
     */
    private class ResultCallback implements LoaderManager.LoaderCallbacks<List<Result>> {
        @Override
        public Loader<List<Result>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Result>>(MovieGridActivity.this) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public List<Result> loadInBackground() {

                    String finalUrl = QueryUtils.getInitialUrl(MovieGridActivity.this, args.getString(getString(R.string.sort_by)));

                    String moviewListJsonResponse = NetworkUtils.makeHTTPRequest(finalUrl);

                    return JSONUtils.parseMovieListJSON(moviewListJsonResponse);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Result>> loader, List<Result> data) {
            //hide the progress bar
            mProgressBar.setVisibility(View.GONE);
            if (data != null && !data.isEmpty())
                mMovieAdapter.setMovieData(data);
            else {
                mMovieAdapter.setMovieData(null);
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    mEmptyView.setText(R.string.no_internet);
                else
                    mEmptyView.setText(R.string.no_data_fetched);
            }
            if(mTwoPane)
                mHandler.sendEmptyMessage(MSG_LOAD_FIRST_ITEM);
        }

        @Override
        public void onLoaderReset(Loader<List<Result>> loader) {

        }
    }

    /**
     * Inner Class representing the callback from the loader used to setup the
     * main ui section when favourites option is selected
     */
    private class CursorCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case FETCH_MOVIE_FROM_DB_ID:
                    return new CursorLoader(mContext, MovieContract.FavouriteEntry.CONTENT_URI, mProjection, null, null, null);
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            List<Result> movieList = new ArrayList<>();
            if (data.moveToFirst()) {
                do {
                    int id = data.getInt(INDEX_MOVIE_ID);
                    String title = data.getString(INDEX_MOVIE_TITLE);
                    String synopsis = data.getString(INDEX_MOVIE_SYNOPSIS);
                    String releaseDate = data.getString(INDEX_MOVIE_RELEASE_DATE);
                    Double userRating = data.getDouble(INDEX_MOVIE_USER_RATING);
                    byte[] posterImage = data.getBlob(INDEX_MOVIE_POSTER);
                    byte[] backdropImage = data.getBlob(INDEX_MOVIE_BACKDROP);

                    Result movie = new Result(id, title, synopsis, releaseDate, userRating, posterImage, backdropImage);
                    movieList.add(movie);
                } while (data.moveToNext());
            }
            mProgressBar.setVisibility(View.GONE);
            mMovieAdapter.setMovieData(movieList);
            if(mTwoPane)
                mHandler.sendEmptyMessage(MSG_LOAD_FIRST_ITEM);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * Function to launch the detail activity
     *
     * @param object
     */
    @Override
    public void onClick(Result object) {
        if(mTwoPane){
            Bundle bundle = new Bundle();
            bundle.putSerializable(MovieDetailFragment.MOVIE_ARG_KEY, object);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(MovieGridActivity.this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailFragment.MOVIE_ARG_KEY, object);
            startActivity(detailIntent);
        }
    }
}