package com.codepath.apps.simpletweets;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.simpletweets.adapters.TweetAdapter;
import com.codepath.apps.simpletweets.models.Tweet;
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

    private TwitterClient client;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private long maxId = 1;
    private long sinceId = 1;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;


    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

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
                populateTimeline();

            }
        };
        rvTweets.addOnScrollListener(scrollListener);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline();
    }

    //Send an API request to get the timeline Json
    private void populateTimeline() {
        client.getHomeTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> newRes = Tweet.fromJSONArray(response);
                if (!newRes.isEmpty()) {
                    maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                    sinceId = newRes.get(0).getUid();
                    tweets.addAll(newRes);
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
