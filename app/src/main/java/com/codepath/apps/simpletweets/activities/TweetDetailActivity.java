package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.TimestampUtils;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {

    private Tweet tweet;
    public static final String TWEET_EXTRA = "tweet";

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        tweet = Parcels.unwrap(intent.getParcelableExtra(TWEET_EXTRA));

        Glide.with(this).load(tweet.getUser().getProfileUrl()).into(ivProfileImage);

        tvBody.setText(tweet.getBody());
        tvUserName.setText(tweet.getUser().getName());
        tvUserScreenName.setText(tweet.getUser().getScreenName());
        tvTimestamp.setText(TimestampUtils.getRelativeTimeAgo(tweet.getCreateAt()));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Twwets");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
