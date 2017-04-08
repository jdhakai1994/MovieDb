package com.example.android.moviedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviedb.adapter.ReviewAdapter;
import com.example.android.moviedb.adapter.TrailerAdapter;
import com.example.android.moviedb.data.MovieContract;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.models.Review;
import com.example.android.moviedb.models.Trailer;
import com.example.android.moviedb.utilities.ImageUtils;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.moviedb.MainActivity.mProjection;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int FETCH_REVIEW_FROM_INTERNET_ID = 12;
    private static final int FETCH_TRAILER_FROM_INTERNET_ID = 13;
    private static final int FETCH_MOVIE_FROM_DB_ID = 22;

    private Result mMovie;
    private Context mContext = DetailActivity.this;
    private Toast mToast;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private TextView mEmptyViewReview;

    private ImageView mPoster;
    private ImageView mBackdrop;

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

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        mMovie = (Result) getIntent().getSerializableExtra("results");

        hookUpMovieDetailUI();
        hookUpMovieReviewUI();
        hookUpMovieTrailerUI();
    }

    /**
     * Helper Method to set up the UI elements of the detail section
     */
    private void hookUpMovieDetailUI() {

        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        if (mMovie.getBackdropImage() == null) {
            String backdropImageUrl = QueryUtils.getBackdropImageUrl(mMovie.getBackdropPath());
            Picasso.with(this)
                    .load(backdropImageUrl)
                    .error(R.drawable.image_error)
                    .into(mBackdrop);
        } else
            mBackdrop.setImageBitmap(ImageUtils.getImage(mMovie.getBackdropImage()));

        mPoster = (ImageView) findViewById(R.id.iv_poster);
        if (mMovie.getPosterImage() == null) {
            String posterImageUrl = QueryUtils.getPosterImageUrlDetail(mMovie.getPosterPath());
            Picasso.with(this)
                    .load(posterImageUrl)
                    .error(R.drawable.image_error)
                    .into(mPoster);
        } else
            mPoster.setImageBitmap(ImageUtils.getImage(mMovie.getPosterImage()));

        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(mMovie.getTitle());

        TextView detailTitle = (TextView) findViewById(R.id.tv_detail_title);
        detailTitle.setText(mMovie.getTitle());

        TextView releaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        releaseDate.setText(mMovie.getReleaseDate());

        TextView voteAverage = (TextView) findViewById(R.id.tv_detail_vote_average);
        String voteAverageData = mMovie.getVoteAverage() + getString(R.string.vote_average_add_on);
        voteAverage.setText(voteAverageData);

        TextView synopsis = (TextView) findViewById(R.id.tv_synopsis_description);
        synopsis.setText(mMovie.getOverview());

        // setting up a loader to check if the movie exists in the database
        getSupportLoaderManager().initLoader(FETCH_MOVIE_FROM_DB_ID, null, new MovieCallback());
    }

    /**
     * Helper Method to set up the UI elements of the review section
     */
    private void hookUpMovieReviewUI() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_review);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        mReviewAdapter = new ReviewAdapter(mContext);
        recyclerView.setAdapter(mReviewAdapter);

        mEmptyViewReview = (TextView) findViewById(R.id.tv_empty_view_review);

        // setting up a loader to fetch the reviews corresponding to the movie from the internet
        getSupportLoaderManager().initLoader(FETCH_REVIEW_FROM_INTERNET_ID, null, new ReviewCallback());
    }

    /**
     * Helper Method to set up the UI elements of the trailer section
     */
    private void hookUpMovieTrailerUI() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_trailer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mTrailerAdapter = new TrailerAdapter(mContext, this);
        recyclerView.setAdapter(mTrailerAdapter);

        // setting up a loader to fetch the trailers corresponding to the movie from the internet
        getSupportLoaderManager().initLoader(FETCH_TRAILER_FROM_INTERNET_ID, null, new TrailerCallback());
    }

    /**
     * Function to launch a trailer
     * @param object
     */
    @Override
    public void onClick(Trailer object) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(QueryUtils.getYouTubeUrl(object.getKey())));
        startActivity(browserIntent);
    }

    /**
     * Inner Class representing the callback from the loader used to setup the
     * review section
     */
    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<Review>> {
        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Review>>(DetailActivity.this) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public List<Review> loadInBackground() {

                    String reviewUrl = QueryUtils.getReviewUrl(mContext, mMovie.getId());

                    String reviewJsonResponse = NetworkUtils.makeHTTPRequest(reviewUrl);

                    return JSONUtils.parseReviewJSON(reviewJsonResponse);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            if (data != null && !data.isEmpty()) {
                mReviewAdapter.setReviewData(data);
                mEmptyViewReview.setVisibility(View.GONE);
            } else {
                mReviewAdapter.setReviewData(null);
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    mEmptyViewReview.setText(R.string.no_internet);
                else
                    mEmptyViewReview.setText(R.string.no_reviews);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    }

    /**
     * Inner Class representing the callback from the loader used to setup the
     * trailer section
     */
    private class TrailerCallback implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(DetailActivity.this) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public List<Trailer> loadInBackground() {

                    String trailerUrl = QueryUtils.getTrailerUrl(mContext, mMovie.getId());

                    String trailerJsonResponse = NetworkUtils.makeHTTPRequest(trailerUrl);

                    return JSONUtils.parseTrailerJSON(trailerJsonResponse);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            if (data != null && !data.isEmpty())
                mTrailerAdapter.setTrailerData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    }

    /**
     * Inner Class representing the callback from the loader used to setup the
     * detail section
     */
    private class MovieCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case FETCH_MOVIE_FROM_DB_ID:
                    Uri fetchUri = MovieContract.FavouriteEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getId().toString()).build();
                    return new CursorLoader(mContext, fetchUri, mProjection, null, null, null);
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            final ImageView favourite = (ImageView) findViewById(R.id.iv_favourite);
            if (data.moveToFirst()) {
                favourite.setImageResource(R.drawable.ic_favourite_solid);
                mMovie.setFavourite(true);
            } else {
                favourite.setImageResource(R.drawable.ic_favourite_hollow);
                mMovie.setFavourite(false);
            }
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMovie.getFavourite()) {
                        mMovie.setFavourite(false);
                        favourite.setImageResource(R.drawable.ic_favourite_hollow);
                        removeDetailsFromDb();
                    } else {
                        mMovie.setFavourite(true);
                        favourite.setImageResource(R.drawable.ic_favourite_solid);
                        storeDetailsInDb();
                    }
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * Helper method used to store the movie details in the database
     */
    private void storeDetailsInDb() {
        ContentValues contentValues = getInputSet();
        Uri uri = getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(mContext, R.string.add_favourites_success, Toast.LENGTH_SHORT);
        } else {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(mContext, R.string.add_favourites_failure, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * Helper method used to delete the movie details from the database
     */
    private void removeDetailsFromDb() {
        Uri deleteUri = MovieContract.FavouriteEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getId().toString()).build();
        int rowsDeleted = getContentResolver().delete(deleteUri, null, null);
        if (rowsDeleted > 0) {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(mContext, R.string.remove_favourites_success, Toast.LENGTH_SHORT);
        } else {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(mContext, R.string.remove_favourites_failure, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * Helper method used to get a set of content values which is supposed to be inserted into the database
     */
    private ContentValues getInputSet() {
        Bitmap posterBitmap = ((BitmapDrawable) mPoster.getDrawable()).getBitmap();
        Bitmap backdropBitmap = ((BitmapDrawable) mBackdrop.getDrawable()).getBitmap();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_TITLE, mMovie.getTitle());
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_SYNOPSIS, mMovie.getOverview());
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_USER_RATING, mMovie.getVoteAverage());
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_POSTER, ImageUtils.getImageBytes(posterBitmap));
        contentValues.put(MovieContract.FavouriteEntry.COLUMN_BACKDROP, ImageUtils.getImageBytes(backdropBitmap));
        return contentValues;
    }
}
