package com.example.android.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviedb.R;
import com.example.android.moviedb.models.Results;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private List<Results> mDataset;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.iv_poster);
        }
    }

    public MovieAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Results object = mDataset.get(position);
        String imageUrl = QueryUtils.getPosterImageUrl(object.getPosterPath());
        Picasso.with(mContext).load(imageUrl).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
        return mDataset.size();
    }

    public void setMovieData(List<Results> movieList){
        mDataset = movieList;
        notifyDataSetChanged();
    }
}
