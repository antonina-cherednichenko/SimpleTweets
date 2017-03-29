package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.adapters.TabFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this));

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);


        setupToolbar(getString(R.string.app_name));
    }


    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

    }


}
