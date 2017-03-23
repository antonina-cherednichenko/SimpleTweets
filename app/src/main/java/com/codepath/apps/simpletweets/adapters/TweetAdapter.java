package com.codepath.apps.simpletweets.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.TimestampUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {

    private List<Tweet> tweets;
    private Context context;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tweetView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvUserScreenName.setText(String.format("@%s", tweet.getUser().getScreenName()));
        holder.tvBody.setText(tweet.getBody());
        holder.tvTimestamp.setText(TimestampUtils.getRelativeTimeAgo(tweet.getCreateAt()));

        Picasso.with(context).load(tweet.getUser().getProfileUrl()).into(holder.ivProfileImage);
    }


    public class TweetViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserScreeName)
        TextView tvUserScreenName;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;
        @BindView(R.id.tvBody)
        TextView tvBody;


        public TweetViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
