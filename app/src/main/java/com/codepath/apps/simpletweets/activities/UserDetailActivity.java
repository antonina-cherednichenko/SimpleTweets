package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.fragments.TweetsFragment;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.simpletweets.TwitterApplication.getRestClient;

public class UserDetailActivity extends AppCompatActivity {

//    @BindView(R.id.flTweets)
//    FrameLayout tweets;

    @BindView(R.id.ivBackground)
    ImageView userBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);

        JsonHttpResponseHandler getAccountUserHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                TwitterApplication.setAccountUser(User.fromJSON(response));

                setUserTweetsFragment();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", throwable.toString());

            }
        };

        if (TwitterApplication.getAccountUser() == null) {
            getRestClient().getUserInfo(getAccountUserHandler);
        } else {
            setUserTweetsFragment();
        }

    }

    private void setUserTweetsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        User accountUser = TwitterApplication.getAccountUser();
        ft.replace(R.id.flTweets, TweetsFragment.newInstance(TweetsFragment.FragmentMode.USER_TIMELINE, accountUser));
        ft.commit();
    }
}
