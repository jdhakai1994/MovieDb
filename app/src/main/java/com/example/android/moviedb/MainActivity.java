package com.example.android.moviedb;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, LoaderManager.LoaderCallbacks<String>{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public final int FETCH_MOVIE_ID = 1;

    private TextView mJsonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mJsonView = (TextView) findViewById(R.id.tv_json);

        LoaderManager.LoaderCallbacks<String> loaderCallback = MainActivity.this;
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));

        getSupportLoaderManager().initLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
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
        LoaderManager.LoaderCallbacks<String> loaderCallback = MainActivity.this;
        Bundle loaderBundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.i_most_popular:
                Toast.makeText(MainActivity.this, "Most Popular", Toast.LENGTH_SHORT).show();
                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.most_popular));
                getSupportLoaderManager().restartLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
                return true;
            case R.id.i_highest_rated:
                Toast.makeText(MainActivity.this, "Highest Rated", Toast.LENGTH_SHORT).show();
                loaderBundle.putString(getString(R.string.sort_by), getString(R.string.highest_rated));
                getSupportLoaderManager().restartLoader(FETCH_MOVIE_ID, loaderBundle, loaderCallback);
                return true;
            default:
                return false;
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(MainActivity.this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String finalUrl = QueryUtils.getUriString(MainActivity.this, args.getString(getString(R.string.sort_by)));

                String JSONResponse = NetworkUtils.makeHTTPRequest(finalUrl);

                return JSONResponse;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        mJsonView.setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

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


}
