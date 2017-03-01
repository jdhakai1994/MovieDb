package com.example.android.moviedb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviedb.models.Results;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    private Results mMovie;

    private ProgressBar mProgressBarBackDrop;
    private ProgressBar mProgressBarPoster;

    private ImageView mBackdrop;
    private ImageView mPoster;

    private ImageView mBackdropErrorImage;
    private ImageView mPosterErrorImage;

    private TextView mTitle;
    private TextView mDetailTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_detail);

        mMovie = (Results) getIntent().getSerializableExtra("results");

        hookUpDetailUI();
    }

    /**
     * Helper Method to set up the UI elements
     */
    private void hookUpDetailUI(){
        mProgressBarBackDrop = (ProgressBar) findViewById(R.id.pb_backdrop_image);
        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        mBackdropErrorImage = (ImageView) findViewById(R.id.iv_backdrop_error_image);
        String backdropImageUrl = QueryUtils.getBackdropImageUrl(mMovie.getBackdropPath());
        Picasso.with(this)
                .load(backdropImageUrl)
                .into(mBackdrop, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (mProgressBarBackDrop != null) {
                            mProgressBarBackDrop.setVisibility(View.GONE);
                            mBackdrop.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError() {
                        if (mProgressBarBackDrop != null) {
                            mProgressBarBackDrop.setVisibility(View.GONE);
                            mBackdropErrorImage.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getApplicationContext(), R.string.error_loading_image_toast, Toast.LENGTH_SHORT).show();
                    }
                });

        mProgressBarPoster = (ProgressBar) findViewById(R.id.pb_poster_image);
        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mPosterErrorImage = (ImageView) findViewById(R.id.iv_poster_error_image);
        String posterImageUrl = QueryUtils.getPosterImageUrlDetail(mMovie.getPosterPath());
        Picasso.with(this)
                .load(posterImageUrl)
                .into(mPoster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (mProgressBarPoster != null) {
                            mProgressBarPoster.setVisibility(View.GONE);
                            mPoster.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError() {
                        if (mProgressBarPoster != null) {
                            mProgressBarPoster.setVisibility(View.GONE);
                            mPosterErrorImage.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getApplicationContext(), R.string.error_loading_image_toast, Toast.LENGTH_SHORT).show();
                    }
                });

        mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText(mMovie.getTitle());

        mDetailTitle = (TextView) findViewById(R.id.tv_detail_title);
        mDetailTitle.setText(mMovie.getTitle());

        mReleaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        mReleaseDate.setText(mMovie.getReleaseDate());

        mVoteAverage = (TextView) findViewById(R.id.tv_detail_vote_average);
        String voteAverageData = mMovie.getVoteAverage() + getString(R.string.vote_average_add_on);
        mVoteAverage.setText(voteAverageData);

        mSynopsis = (TextView) findViewById(R.id.tv_synopsis_description);
        mSynopsis.setText(mMovie.getOverview());
    }

}
