package com.example.android.moviedb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.moviedb.adapter.ReviewAdapter;
import com.example.android.moviedb.adapter.TrailerAdapter;
import com.example.android.moviedb.data.MovieContract;
import com.example.android.moviedb.databinding.FragmentMovieDetailBinding;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.models.Review;
import com.example.android.moviedb.models.Trailer;
import com.example.android.moviedb.utilities.ImageUtils;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.moviedb.MovieGridActivity.mProjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements TrailerAdapter.ListItemClickListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final int FETCH_REVIEW_FROM_INTERNET_ID = 12;
    private static final int FETCH_TRAILER_FROM_INTERNET_ID = 13;
    private static final int FETCH_MOVIE_FROM_DB_ID = 22;

    public static final String MOVIE_ARG_KEY = "movie_key";

    private Result mMovie;

    private Toast mToast;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private FragmentMovieDetailBinding mFragmentMovieDetailBinding;
    private android.content.Context mContext;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        //get the movie object passed
        if (getArguments().containsKey(MOVIE_ARG_KEY))
            mMovie = (Result) getArguments().getSerializable(MOVIE_ARG_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentMovieDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);
        View rootView = mFragmentMovieDetailBinding.getRoot();

        hookUpMovieDetailUI();
        hookUpMovieReviewUI();
        hookUpMovieTrailerUI();

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //important to preserve the state of the favourite button
        getActivity().getSupportLoaderManager().restartLoader(FETCH_MOVIE_FROM_DB_ID, null, new MovieCallback());
    }

    /**
     * Helper Method to launch a trailer
     *
     * @param object
     */
    @Override
    public void onClick(Trailer object) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(QueryUtils.getYouTubeUrl(object.getKey())));
        startActivity(browserIntent);
    }

    /**
     * Helper Method to set up the UI elements of the detail section
     */
    private void hookUpMovieDetailUI() {

        String voteAverageData = mMovie.getVoteAverage() + getString(R.string.vote_average_add_on);

        if (mMovie.getBackdropImage() == null) {
            String backdropImageUrl = QueryUtils.getBackdropImageUrl(mMovie.getBackdropPath());
            Picasso.with(mContext)
                    .load(backdropImageUrl)
                    .error(R.drawable.image_error)
                    .into(mFragmentMovieDetailBinding.movieDetail.ivBackdrop);
        } else
            mFragmentMovieDetailBinding.movieDetail.ivBackdrop.setImageBitmap(ImageUtils.getImage(mMovie.getBackdropImage()));

        if (mMovie.getPosterImage() == null) {
            String posterImageUrl = QueryUtils.getPosterImageUrlDetail(mMovie.getPosterPath());
            Picasso.with(mContext)
                    .load(posterImageUrl)
                    .error(R.drawable.image_error)
                    .into(mFragmentMovieDetailBinding.movieDetail.ivPoster);
        } else
            mFragmentMovieDetailBinding.movieDetail.ivPoster.setImageBitmap(ImageUtils.getImage(mMovie.getPosterImage()));

        mFragmentMovieDetailBinding.movieDetail.tvTitle.setText(mMovie.getTitle());
        mFragmentMovieDetailBinding.movieDetail.tvDetailTitle.setText(mMovie.getTitle());
        mFragmentMovieDetailBinding.movieDetail.tvDetailReleaseDate.setText(mMovie.getReleaseDate());
        mFragmentMovieDetailBinding.movieDetail.tvDetailVoteAverage.setText(voteAverageData);
        mFragmentMovieDetailBinding.movieDetail.tvSynopsisDescription.setText(mMovie.getOverview());

        // setting up a loader to check if the movie exists in the database
        getActivity().getSupportLoaderManager().initLoader(FETCH_MOVIE_FROM_DB_ID, null, new MovieCallback());
    }

    /**
     * Helper Method to set up the UI elements of the review section
     */
    private void hookUpMovieReviewUI() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mFragmentMovieDetailBinding.movieReview.rvReview.setLayoutManager(layoutManager);
        mFragmentMovieDetailBinding.movieReview.rvReview.setNestedScrollingEnabled(false);

        mReviewAdapter = new ReviewAdapter(mContext);
        mFragmentMovieDetailBinding.movieReview.rvReview.setAdapter(mReviewAdapter);

        // setting up a loader to fetch the reviews corresponding to the movie from the internet
        getActivity().getSupportLoaderManager().restartLoader(FETCH_REVIEW_FROM_INTERNET_ID, null, new ReviewCallback());
    }

    /**
     * Helper Method to set up the UI elements of the trailer section
     */
    private void hookUpMovieTrailerUI() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mFragmentMovieDetailBinding.movieTrailer.rvTrailer.setLayoutManager(layoutManager);

        mTrailerAdapter = new TrailerAdapter(mContext, this);
        mFragmentMovieDetailBinding.movieTrailer.rvTrailer.setAdapter(mTrailerAdapter);

        // setting up a loader to fetch the trailer corresponding to the movie from the internet
        getActivity().getSupportLoaderManager().restartLoader(FETCH_TRAILER_FROM_INTERNET_ID, null, new TrailerCallback());
    }

    /**
     * Helper method used to store the movie details in the database
     */
    private void storeDetailsInDb() {
        ContentValues contentValues = getInputSet();
        Uri uri = mContext.getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
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
        int rowsDeleted = mContext.getContentResolver().delete(deleteUri, null, null);
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
        Bitmap posterBitmap = ((BitmapDrawable) mFragmentMovieDetailBinding.movieDetail.ivPoster.getDrawable()).getBitmap();
        Bitmap backdropBitmap = ((BitmapDrawable) mFragmentMovieDetailBinding.movieDetail.ivBackdrop.getDrawable()).getBitmap();

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
            if (data.moveToFirst()) {
                mFragmentMovieDetailBinding.movieDetail.ivFavourite.setImageResource(R.drawable.ic_favourite_solid);
                mMovie.setFavourite(true);
            } else {
                mFragmentMovieDetailBinding.movieDetail.ivFavourite.setImageResource(R.drawable.ic_favourite_hollow);
                mMovie.setFavourite(false);
            }
            mFragmentMovieDetailBinding.movieDetail.ivFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMovie.getFavourite()) {
                        mMovie.setFavourite(false);
                        mFragmentMovieDetailBinding.movieDetail.ivFavourite.setImageResource(R.drawable.ic_favourite_hollow);
                        removeDetailsFromDb();
                    } else {
                        mMovie.setFavourite(true);
                        mFragmentMovieDetailBinding.movieDetail.ivFavourite.setImageResource(R.drawable.ic_favourite_solid);
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
     * Inner Class representing the callback from the loader used to setup the
     * review section
     */
    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<Review>> {
        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Review>>(mContext) {

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
                mFragmentMovieDetailBinding.movieReview.tvEmptyViewReview.setVisibility(View.GONE);
            } else {
                mReviewAdapter.setReviewData(null);
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    mFragmentMovieDetailBinding.movieReview.tvEmptyViewReview.setText(R.string.no_internet);
                else
                    mFragmentMovieDetailBinding.movieReview.tvEmptyViewReview.setText(R.string.no_reviews);
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
            return new AsyncTaskLoader<List<Trailer>>(mContext) {

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
            if (data != null && !data.isEmpty()) {
                mTrailerAdapter.setTrailerData(data);
                mFragmentMovieDetailBinding.movieTrailer.tvEmptyViewTrailer.setVisibility(View.GONE);
            } else {
                mTrailerAdapter.setTrailerData(null);
                if (!NetworkUtils.isConnectedToInternet(mContext))
                    mFragmentMovieDetailBinding.movieTrailer.tvEmptyViewTrailer.setText(R.string.no_internet);
                else
                    mFragmentMovieDetailBinding.movieTrailer.tvEmptyViewTrailer.setText(R.string.no_trailers);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    }
}