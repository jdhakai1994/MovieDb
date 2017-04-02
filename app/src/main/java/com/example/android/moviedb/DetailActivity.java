package com.example.android.moviedb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviedb.adapter.ReviewAdapter;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.models.Review;
import com.example.android.moviedb.models.Trailer;
import com.example.android.moviedb.utilities.JSONUtils;
import com.example.android.moviedb.utilities.NetworkUtils;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int FETCH_REVIEW_ID = 12;
    private static final int FETCH_TRAILER_ID = 13;

    private Result mMovie;
    private Context mContext = DetailActivity.this;

    private ReviewAdapter mReviewAdapter;
    private TextView mEmptyViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_detail);

        mMovie = (Result) getIntent().getSerializableExtra("results");

        Log.d(LOG_TAG, mMovie.getId().toString());

        hookUpMovieDetailUI();
        hookUpMovieReviewUI();
        hookUpMovieTrailerUI();
    }

    /**
     * Helper Method to set up the UI elements of the detail
     */
    private void hookUpMovieDetailUI() {
        ImageView backdrop = (ImageView) findViewById(R.id.iv_backdrop);
        String backdropImageUrl = QueryUtils.getBackdropImageUrl(mMovie.getBackdropPath());
        Picasso.with(this)
                .load(backdropImageUrl)
                .error(R.drawable.image_error)
                .into(backdrop);

        ImageView poster = (ImageView) findViewById(R.id.iv_poster);
        String posterImageUrl = QueryUtils.getPosterImageUrlDetail(mMovie.getPosterPath());
        Picasso.with(this)
                .load(posterImageUrl)
                .error(R.drawable.image_error)
                .into(poster);

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
    }

    /**
     * Helper Method to set up the UI elements of the review
     */
    private void hookUpMovieReviewUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_review);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        mReviewAdapter = new ReviewAdapter(mContext);
        recyclerView.setAdapter(mReviewAdapter);

        mEmptyViewReview = (TextView) findViewById(R.id.tv_empty_view_review);

        getSupportLoaderManager().initLoader(FETCH_REVIEW_ID, null, new ReviewCallback());
    }

    /**
     * Helper Method to set up the UI elements of the trailer
     */
    private void hookUpMovieTrailerUI() {
        getSupportLoaderManager().initLoader(FETCH_TRAILER_ID, null, new TrailerCallback());
    }

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
            Log.d(LOG_TAG, String.valueOf(data.size()));
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    }
}
