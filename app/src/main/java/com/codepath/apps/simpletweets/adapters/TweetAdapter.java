package com.codepath.apps.simpletweets.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.activities.TweetDetailActivity;
import com.codepath.apps.simpletweets.activities.UserDetailActivity;
import com.codepath.apps.simpletweets.fragments.AddNewTweetDialog;
import com.codepath.apps.simpletweets.models.Media;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.PatternEditableBuilder;
import com.codepath.apps.simpletweets.utils.TimestampUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> implements AddNewTweetDialog.AddTweetListener {

    private List<Tweet> tweets;
    private Context context;
    private static TwitterClient client;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
        this.client = TwitterApplication.getRestClient();
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
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        text -> {
                        }).addPattern(Pattern.compile("\\#(\\w+)"), Color.BLUE, text -> {
        }).into(holder.tvBody);
        holder.tvTimestamp.setText(TimestampUtils.getRelativeTimeAgo(tweet.getCreateAt()));

        holder.ivBodyImage.setImageDrawable(null);

        Glide.with(context).load(tweet.getUser().getProfileUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().into(holder.ivProfileImage);

        Media tweetMedia = tweet.getMedia();

        if (tweetMedia != null && tweetMedia.getType().equals("photo")) {
            Glide.with(context).load(tweetMedia.getMediaUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter().into(holder.ivBodyImage);
        }

    }

    JsonHttpResponseHandler replyRetweetHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            //Tweet newTweet = Tweet.fromJSON(response, false);

            //tweets.add(0, newTweet);
            //notifyDataSetChanged();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("ERROR", errorResponse.toString());
        }
    };

    @Override
    public void addTweet(String tweetBody, Long tweetId) {
        client.replyTweet(tweetId, tweetBody, replyRetweetHandler);
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
        @BindView(R.id.btnReply)
        ImageButton btnReply;
        @BindView(R.id.btnRetweet)
        ImageButton btnRetweet;


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

            ivProfileImage.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Tweet tweet = tweets.get(position);

                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.ARG_USER, Parcels.wrap(tweet.getUser()));
                context.startActivity(intent);

            });

            btnReply.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Tweet tweet = tweets.get(position);

                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                AddNewTweetDialog addDialog = AddNewTweetDialog.newInstance(tweet.getUid());
                addDialog.show(fm, "fragment_filter_dialog");


            });

            btnRetweet.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Tweet tweet = tweets.get(position);
                client.retweetTweet(tweet.getUid(), replyRetweetHandler);
            });


        }
    }
}
