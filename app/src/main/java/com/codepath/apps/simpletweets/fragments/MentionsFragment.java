package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.TweetAdapter;
import com.codepath.apps.simpletweets.db.TwitterDatabase;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.Tweet_Table;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class MentionsFragment extends Fragment {

    private enum WorkMode {
        SCROLL,
        REFRESH,
        INIT
    }


    private TwitterClient client;
    private TweetAdapter adapter;
    private List<Tweet> mentions;
    private long maxId = 1;
    private long sinceId = 1;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.swipeContainerMentions)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.rvMentions)
    RecyclerView rvMentions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentions, container, false);

        ButterKnife.bind(this, view);

        //Get mentions from SQLite database
        DatabaseDefinition database = FlowManager.getDatabase(TwitterDatabase.class);
        if (database != null) {
            mentions = SQLite.select().
                    from(Tweet.class).where(Tweet_Table.isMention.is(true)).orderBy(Tweet_Table.uid, false).queryList();
        } else {
            mentions = new ArrayList<>();
        }


        adapter = new TweetAdapter(getContext(), mentions);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvMentions.setLayoutManager(llm);
        rvMentions.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // Create the Handler object (on the main thread by default)
                populateTimeline(WorkMode.SCROLL);

            }
        };
        rvMentions.addOnScrollListener(scrollListener);


        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            populateTimeline(WorkMode.REFRESH);
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        client = TwitterApplication.getRestClient(); //singleton client


        populateTimeline(WorkMode.INIT);

        return view;
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

        client.getMentions(maxIdVal, sinceIdVal, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newRes = Tweet.fromJSONArray(response, true);
                if (!newRes.isEmpty()) {
                    List<Tweet> newUniqueMentions;
                    if (mode == WorkMode.REFRESH) {
                        sinceId = newRes.get(0).getUid();
                        newUniqueMentions = Tweet.getUniqueTweets(mentions, newRes);
                        mentions.addAll(0, newUniqueMentions);

                    } else if (mode == WorkMode.SCROLL) {
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        newUniqueMentions = Tweet.getUniqueTweets(mentions, newRes);
                        mentions.addAll(newUniqueMentions);

                    } else {
                        sinceId = newRes.get(0).getUid();
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        newUniqueMentions = Tweet.getUniqueTweets(mentions, newRes);
                        mentions.addAll(newUniqueMentions);
                    }

                    adapter.notifyDataSetChanged();

                    FastStoreModelTransaction<Tweet> fsmt = FastStoreModelTransaction
                            .saveBuilder(FlowManager.getModelAdapter(Tweet.class))
                            .addAll(newUniqueMentions)
                            .build();

                    DatabaseDefinition database = FlowManager.getDatabase(TwitterDatabase.class);

                    Transaction transaction = database.beginTransactionAsync(fsmt)
                            .success(transactionSuccess -> {
                                // This runs on UI thread

                            }).error((transactionError, error) -> Log.e("ServiceError", error.getMessage())).build();
                    transaction.execute();

                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", throwable.toString());
                swipeContainer.setRefreshing(false);

            }
        });
    }

}
