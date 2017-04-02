package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.TabFragmentPagerAdapter;
import com.codepath.apps.simpletweets.fragments.AddNewTweetDialog;
import com.codepath.apps.simpletweets.fragments.TweetsFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.simpletweets.TwitterApplication.getRestClient;

public class TimelineActivity extends AppCompatActivity implements AddNewTweetDialog.AddTweetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.fabAddTweet)
    FloatingActionButton fabAddTweet;

    private TabFragmentPagerAdapter tabAdapter;
    private TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        setupToolbar("");


        client = TwitterApplication.getRestClient(); //singleton client

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        tabAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this);
        viewPager.setAdapter(tabAdapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);


        fabAddTweet.setOnClickListener(v -> {

            if (TwitterApplication.getAccountUser() == null) {
                getRestClient().getUserInfo(getAccountUserHandler(() -> showAddTweetFragment()));
            } else {
                showAddTweetFragment();
            }

        });

    }

    private void showAddTweetFragment() {
        FragmentManager fm = getSupportFragmentManager();
        AddNewTweetDialog addDialog = AddNewTweetDialog.newInstance();
        addDialog.show(fm, "fragment_filter_dialog");
    }


    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setTitle(title);

    }


    private interface OnAccountUserSetListener {
        void runAfterUserSet();
    }

    private JsonHttpResponseHandler getAccountUserHandler(OnAccountUserSetListener listener) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                TwitterApplication.setAccountUser(User.fromJSON(response));

                listener.runAfterUserSet();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", throwable.toString());

            }
        };

    }


    private void startAccountUserDetailActivity() {
        Intent intent = new Intent(TimelineActivity.this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.ARG_USER, Parcels.wrap(TwitterApplication.getAccountUser()));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_user:
                if (TwitterApplication.getAccountUser() == null) {
                    getRestClient().getUserInfo(getAccountUserHandler(() -> startAccountUserDetailActivity()));
                } else {
                    startAccountUserDetailActivity();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tabAdapter.searchQuery = query;
                tabAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                tabAdapter.searchQuery = null;
                tabAdapter.notifyDataSetChanged();
                return true;
            }
        });


        return true;
    }


    @Override
    public void addTweet(String tweetBody, Long tweetId) {
        client.addNewTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJSON(response, false);

                TweetsFragment fragment = (TweetsFragment) tabAdapter.getRegisteredFragment(0);
                if (fragment != null) {
                    fragment.addNewTweet(newTweet);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ERROR", errorResponse.toString());
            }
        });
    }


}
