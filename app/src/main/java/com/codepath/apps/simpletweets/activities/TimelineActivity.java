package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.TweetAdapter;
import com.codepath.apps.simpletweets.fragments.AddNewTweetDialog;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private enum WorkMode {
        SCROLL,
        REFRESH,
        INIT
    }

    private TwitterClient client;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private long maxId = 1;
    private long sinceId = 1;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    private User currentUser;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fabAddTweet)
    FloatingActionButton fabAddTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTweets.setLayoutManager(llm);
        rvTweets.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // Create the Handler object (on the main thread by default)
                populateTimeline(WorkMode.SCROLL);

            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        setupToolbar();


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(WorkMode.REFRESH);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        client = TwitterApplication.getRestClient(); //singleton client


        // Set User object
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        });


        fabAddTweet.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddNewTweetDialog addDialog = AddNewTweetDialog.newInstance();
                addDialog.show(fm, "fragment_filter_dialog");
            }
        });


        populateTimeline(WorkMode.INIT);


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

    }


    //Send an API request to get the timeline Json
    private void populateTimeline(final WorkMode mode) {
        Long sinceIdVal = null;
        Long maxIdVal = null;

        if (mode == WorkMode.INIT) {
            sinceIdVal = 1L;
        } else if (mode == WorkMode.REFRESH) {
            sinceIdVal = this.sinceId;
        } else if (mode == WorkMode.SCROLL) {
            maxIdVal = this.maxId;
        }

        client.getHomeTimeline(maxIdVal, sinceIdVal, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newRes = Tweet.fromJSONArray(response);
                if (!newRes.isEmpty()) {
                    if (mode == WorkMode.REFRESH) {
                        sinceId = newRes.get(0).getUid();
                        tweets.addAll(0, newRes);
                    } else if (mode == WorkMode.SCROLL) {
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        tweets.addAll(newRes);
                    } else {
                        sinceId = newRes.get(0).getUid();
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        tweets.addAll(newRes);
                    }

                    adapter.notifyDataSetChanged();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        });
    }
}