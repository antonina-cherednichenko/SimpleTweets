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
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;


public class TweetsFragment extends Fragment {

    public enum FragmentMode {
        TIMELINE,
        MENTIONS,
        USER_TIMELINE,
        SEARCH
    }

    private enum WorkMode {
        SCROLL,
        REFRESH,
        INIT
    }

    public static final String ARG_MODE = "ARG_MODE";
    public static final String ARG_USER = "ARG_USER";
    public static final String ARG_QUERY = "ARG_QUERY";

    private FragmentMode fragmentMode;

    private TwitterClient client;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private long maxId = 1;
    private long sinceId = 1;

    private User user;
    private String query;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;


    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private DatabaseDefinition database = FlowManager.getDatabase(TwitterDatabase.class);


    public static TweetsFragment newInstance(FragmentMode mode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);

        TweetsFragment fragment = new TweetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TweetsFragment newInstance(FragmentMode mode, User user) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        args.putParcelable(ARG_USER, Parcels.wrap(user));


        TweetsFragment fragment = new TweetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TweetsFragment newInstance(FragmentMode mode, String query) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        args.putString(ARG_QUERY, query);


        TweetsFragment fragment = new TweetsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentMode = (FragmentMode) getArguments().getSerializable(ARG_MODE);
        user = Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        query = getArguments().getString(ARG_QUERY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets, container, false);

        ButterKnife.bind(this, view);

        //Get tweets from SQLite database
        getSavedTweetsFromDB();

        adapter = new TweetAdapter(getContext(), tweets);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
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

    private void getSavedTweetsFromDB() {
        if (database != null) {
            switch (fragmentMode) {
                case TIMELINE:
                    tweets = select().
                            from(Tweet.class).orderBy(Tweet_Table.uid, false).queryList();
                    break;
                case MENTIONS:
                    tweets = select().
                            from(Tweet.class).where(Tweet_Table.isMention.is(true)).orderBy(Tweet_Table.uid, false).queryList();
                    break;
                case USER_TIMELINE:
                    if (user != null) {
                        tweets = SQLite.select().
                                from(Tweet.class).where(Tweet_Table.user_uid.is(user.getUid())).orderBy(Tweet_Table.uid, false).queryList();
                    } else {
                        tweets = new ArrayList<>();
                    }

                    break;
                case SEARCH:
                    tweets = new ArrayList<>();
                    break;
            }
        } else {
            tweets = new ArrayList<>();
        }


    }

    //Send an API request to get the timeline Json
    private void populateTimeline(final WorkMode workMode) {
        Long sinceIdVal = null;
        Long maxIdVal = null;

        if (workMode == WorkMode.INIT) {
            sinceIdVal = 1L;
        } else if (workMode == WorkMode.REFRESH) {
            sinceIdVal = this.sinceId;
        } else if (workMode == WorkMode.SCROLL) {
            maxIdVal = this.maxId;
        }

        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    List<Tweet> newRes = Tweet.fromJSONArray(response.getJSONArray("statuses"));
                    List<Tweet> newUniqueMentions = Tweet.getUniqueTweets(tweets, newRes);
                    tweets.addAll(0, newUniqueMentions);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newRes = Tweet.fromJSONArray(response, fragmentMode == FragmentMode.MENTIONS);
                if (!newRes.isEmpty()) {
                    List<Tweet> newUniqueMentions;
                    if (workMode == WorkMode.REFRESH) {
                        sinceId = newRes.get(0).getUid();
                        newUniqueMentions = Tweet.getUniqueTweets(tweets, newRes);
                        tweets.addAll(0, newUniqueMentions);

                    } else if (workMode == WorkMode.SCROLL) {
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        newUniqueMentions = Tweet.getUniqueTweets(tweets, newRes);
                        tweets.addAll(newUniqueMentions);

                    } else {
                        sinceId = newRes.get(0).getUid();
                        maxId = newRes.get(newRes.size() - 1).getUid() - 1;
                        newUniqueMentions = Tweet.getUniqueTweets(tweets, newRes);
                        tweets.addAll(newUniqueMentions);
                    }

                    adapter.notifyDataSetChanged();

                    FastStoreModelTransaction<Tweet> fsmt = FastStoreModelTransaction
                            .saveBuilder(FlowManager.getModelAdapter(Tweet.class))
                            .addAll(newUniqueMentions)
                            .build();

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

        };

        switch (fragmentMode) {
            case TIMELINE:
                client.getHomeTimeline(maxIdVal, sinceIdVal, handler);
                break;
            case MENTIONS:
                client.getMentions(maxIdVal, sinceIdVal, handler);
                break;
            case USER_TIMELINE:
                client.getUserTimeline(user.getUid(), user.getScreenName(), maxIdVal, sinceIdVal, handler);
                break;
            case SEARCH:
                client.getSearchTweets(query, handler);
                break;
        }

    }

    public void addNewTweet(Tweet newTweet) {
        if (!tweets.contains(newTweet)) {
            newTweet.save();

            tweets.add(0, newTweet);
            adapter.notifyItemChanged(0);
        }
    }


}
