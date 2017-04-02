package com.example.android.moviedb;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, LoaderManager.LoaderCallbacks<List<Result>>, MovieAdapter.GridItemClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public final int FETCH_MOVIE_ID = 11;

    private MovieAdapter mMovieAdapter;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private Context mContext = MainActivity.this;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_main_ui);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view_main_ui);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_main_ui);
        RecyclerView.LayoutManager mLayoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mLayoutManager = new GridLayoutManager(mContext, 2);
        else
            mLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(mLayoutManager);

        mMovieAdapter = new MovieAdapter(MainActivity.this, this);
        recyclerView.setAdapter(mMovieAdapter);

        LoaderManager.LoaderCallbacks<List<Result>> loaderCallback = MainActivity.this;
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));

        // if the device is not connected to internet change the text of the empty view
        if (!NetworkUtils.isConnectedToInternet(mContext))
            showNoInternetUI();
        else {
            showFetchingDataUI();
            // Initialise the custom loader
            getSupportLoaderManager().initLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
        LoaderManager.LoaderCallbacks<List<Result>> loaderCallback = MainActivity.this;
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
                    getSupportLoaderManager().restartLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
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
                    getSupportLoaderManager().restartLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
                }
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
        PopupMenu popup = new PopupMenu(MainActivity.this, v);

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

    @Override
    public Loader<List<Result>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Result>>(MainActivity.this) {

            @Override
            protected void onStartLoading() {
                    forceLoad();
            }

            @Override
            public List<Result> loadInBackground() {

                String finalUrl = QueryUtils.getInitialUrl(MainActivity.this, args.getString(getString(R.string.sort_by)));

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
            if(!NetworkUtils.isConnectedToInternet(mContext))
                mEmptyView.setText(R.string.no_internet);
            else
                mEmptyView.setText(R.string.no_data_fetched);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Result>> loader) {

    }

    @Override
    public void onClick(Result object) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra("results", object);
        startActivity(detailIntent);
    }
}
