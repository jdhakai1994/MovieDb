package com.example.android.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviedb.R;
import com.example.android.moviedb.models.Trailer;
import com.example.android.moviedb.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jayabrata Dhakai on 4/2/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mDataset;
    private Context mContext;

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onClick(Trailer object);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView trailer;

        public ViewHolder(View v) {
            super(v);
            trailer = (ImageView) v.findViewById(R.id.iv_trailer);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = mDataset.get(adapterPosition);
            mOnClickListener.onClick(trailer);
        }
    }

    public TrailerAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trailer object = mDataset.get(position);
        String imageUrl = QueryUtils.getVideoImageUrl(object.getKey());
        Picasso.with(mContext).load(imageUrl).into(holder.trailer);
    }

    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
        return mDataset.size();
    }

    public void setTrailerData(List<Trailer> trailerList){
        mDataset = trailerList;
        notifyDataSetChanged();
    }
}

