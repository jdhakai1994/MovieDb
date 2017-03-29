package com.example.android.moviedb;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviedb.models.Results;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    private Results mMovie;

    private ImageView mBackdrop;
    private ImageView mPoster;

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
        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        String backdropImageUrl = QueryUtils.getBackdropImageUrl(mMovie.getBackdropPath());
        Picasso.with(this)
                .load(backdropImageUrl)
                .error(R.drawable.image_error)
                .into(mBackdrop);

        mPoster = (ImageView) findViewById(R.id.iv_poster);
        String posterImageUrl = QueryUtils.getPosterImageUrlDetail(mMovie.getPosterPath());
        Picasso.with(this)
                .load(posterImageUrl)
                .error(R.drawable.image_error)
                .into(mPoster);

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
