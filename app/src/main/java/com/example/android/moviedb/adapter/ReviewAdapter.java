package com.example.android.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.android.moviedb.R;
import com.example.android.moviedb.models.Review;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;

/**
 * Created by Jayabrata Dhakai on 4/2/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> mDataset;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView author;
        ExpandableTextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            content = (ExpandableTextView) itemView.findViewById(R.id.tv_review_content);
            content.setOnClickListener(this);
            content.setInterpolator(new OvershootInterpolator());
        }

        @Override
        public void onClick(View v) {
            content.toggle();
        }
    }

    public ReviewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Review object = mDataset.get(position);
        holder.author.setText(object.getAuthor());
        holder.content.setText(object.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mDataset) return 0;
        return mDataset.size();
    }

    public void setReviewData(List<Review> reviewList){
        mDataset = reviewList;
        notifyDataSetChanged();
    }

}
