package com.example.android.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviedb.R;
import com.example.android.moviedb.models.Result;
import com.example.android.moviedb.utilities.ImageUtils;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private List<Result> mDataset;
    private Context mContext;

    final private GridItemClickListener mOnClickListener;

    public interface GridItemClickListener {
        void onClick(Result object);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView poster;

        public ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.iv_poster);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result movie = mDataset.get(adapterPosition);
            mOnClickListener.onClick(movie);
        }
    }

    public MovieAdapter(Context context, GridItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_poster, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result object = mDataset.get(position);
        if(object.getPosterImage() == null) {
            String imageUrl = QueryUtils.getPosterImageUrl(object.getPosterPath());
            Picasso.with(mContext).load(imageUrl).into(holder.poster);
        }else{
            holder.poster.setImageBitmap(ImageUtils.getImage(object.getPosterImage()));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
            return mDataset.size();
    }

    public void setMovieData(List<Result> movieList) {
        mDataset = movieList;
        notifyDataSetChanged();
    }

    public Result getFirstMovieData() {
        return mDataset.get(0);
    }
}