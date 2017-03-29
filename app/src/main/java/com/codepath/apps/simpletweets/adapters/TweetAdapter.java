package com.codepath.apps.simpletweets.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.TweetDetailActivity;
import com.codepath.apps.simpletweets.models.Media;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.TimestampUtils;

import org.parceler.Parcels;

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
        View tweetView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_for_adapter, parent, false);
        return new TweetViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvUserScreenName.setText(String.format("@%s", tweet.getUser().getScreenName()));
        holder.tvBody.setText(tweet.getBody());
        holder.tvTimestamp.setText(TimestampUtils.getRelativeTimeAgo(tweet.getCreateAt()));

        Media tweetMedia = tweet.getMedia();
        if (tweetMedia == null) {
            return;
        }


        if (tweetMedia.getType().equals("photo")) {
            Glide.with(context).load(tweetMedia.getMediaUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter().into(holder.ivBodyImage);
        } else {
            holder.ivBodyImage.setImageResource(0);
        }

        Glide.with(context).load(tweet.getUser().getProfileUrl()).into(holder.ivProfileImage);
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
        @BindView(R.id.ivBodyImage)
        ImageView ivBodyImage;


        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Tweet tweet = tweets.get(position);

                Context context = itemView.getContext();
                Intent intent = new Intent(context, TweetDetailActivity.class);
                intent.putExtra(TweetDetailActivity.TWEET_EXTRA, Parcels.wrap(tweet));
                context.startActivity(intent);

            });


        }
    }
}
