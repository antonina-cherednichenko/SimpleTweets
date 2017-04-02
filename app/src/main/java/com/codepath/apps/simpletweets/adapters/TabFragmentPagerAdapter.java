package com.codepath.apps.simpletweets.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.simpletweets.fragments.TweetsFragment;

public class TabFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Timeline", "Mentions"};
    private Context context;

    public String searchQuery = null;

    public TabFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (searchQuery != null) {
                return TweetsFragment.newInstance(TweetsFragment.FragmentMode.SEARCH, searchQuery);
            } else {
                return TweetsFragment.newInstance(TweetsFragment.FragmentMode.TIMELINE);
            }

        } else {

            return TweetsFragment.newInstance(TweetsFragment.FragmentMode.MENTIONS);
        }


    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
