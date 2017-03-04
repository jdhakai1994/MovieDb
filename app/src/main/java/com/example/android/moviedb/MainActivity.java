package com.example.android.moviedb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviedb.adapter.MovieAdapter;
import com.example.android.moviedb.models.Results;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, LoaderManager.LoaderCallbacks<List<Results>>, MovieAdapter.GridItemClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public final int FETCH_MOVIE_ID = 1;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_main_ui);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_ui);
        mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMovieAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        LoaderManager.LoaderCallbacks<List<Results>> loaderCallback = MainActivity.this;
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));

        // if the device is not connected to internet change the text of the empty view
        if(!isConnectedToInternet()) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setText(getString(R.string.no_internet));
        } else{
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
        LoaderManager.LoaderCallbacks<List<Results>> loaderCallback = MainActivity.this;
        Bundle loaderBundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.i_most_popular:
                Toast.makeText(MainActivity.this, "Most Popular", Toast.LENGTH_SHORT).show();
                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));
                // if the device is not connected to internet change the text of the empty view
                if(!isConnectedToInternet()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyView.setText(getString(R.string.no_internet));
                } else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    // Initialise the custom loader
                    getSupportLoaderManager().restartLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
                }
                return true;
            case R.id.i_highest_rated:
                Toast.makeText(MainActivity.this, "Highest Rated", Toast.LENGTH_SHORT).show();
                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.highest_rated));
                // if the device is not connected to internet change the text of the empty view
                if(!isConnectedToInternet()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyView.setText(getString(R.string.no_internet));
                } else{
                    mProgressBar.setVisibility(View.VISIBLE);
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
     * @param v is the view to which the pop-up will be attached
     */
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.sortby_menu);
        popup.show();
    }

    /*
     * Helper method to check if the device is connected to the internet
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public Loader<List<Results>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Results>>(MainActivity.this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<Results> loadInBackground() {

                String finalUrl = QueryUtils.getQueryUrl(MainActivity.this, args.getString(getString(R.string.sort_by)));

                String JSONResponse = NetworkUtils.makeHTTPRequest(finalUrl);

                List<Results> movieList = JSONUtils.parseJSON(JSONResponse);

                return movieList;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Results>> loader, List<Results> data) {
        //hide the progress bar
        mProgressBar.setVisibility(View.GONE);
        if (data != null && !data.isEmpty())
            mMovieAdapter.setMovieData(data);
        else
            mEmptyView.setText(R.string.no_data_fetched);
    }

    @Override
    public void onLoaderReset(Loader<List<Results>> loader) {

    }

    @Override
    public void onClick(Results object) {
        Log.d(LOG_TAG, object.getTitle());
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra("results", object);
        startActivity(detailIntent);
    }
}
