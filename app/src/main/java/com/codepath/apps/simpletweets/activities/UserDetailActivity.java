package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.TweetsFragment;
import com.codepath.apps.simpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDetailActivity extends AppCompatActivity {

//    @BindView(R.id.flTweets)
//    FrameLayout tweets;

    @BindView(R.id.ivProfileBackground)
    ImageView ivProfileBackground;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvName)
    TextView tvUserName;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.tvFollowersCount)
    TextView tvFollowersCount;

    @BindView(R.id.tvFollowingCount)
    TextView tvFollowingCount;

    @BindView(R.id.tvTagline)
    TextView tvTagLine;


    public final static String ARG_USER = "user";

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));

        tvFollowersCount.setText(String.valueOf(user.getFollowers()));
        tvFollowingCount.setText(String.valueOf(user.getFollowing()));
        tvUserName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvTagLine.setText(user.getTagline());

        Glide.with(this).load(user.getProfileBannerUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().into(ivProfileBackground);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTweets, TweetsFragment.newInstance(TweetsFragment.FragmentMode.USER_TIMELINE, user));
        ft.commit();

        setupToolbar();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(user.getScreenName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
