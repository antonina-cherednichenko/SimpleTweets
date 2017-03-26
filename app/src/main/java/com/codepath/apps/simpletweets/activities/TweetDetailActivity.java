package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    private Tweet tweet;
    public static final String TWEET_EXTRA = "tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Intent intent = getIntent();
        tweet = Parcels.unwrap(intent.getParcelableExtra(TWEET_EXTRA));
    }
}
