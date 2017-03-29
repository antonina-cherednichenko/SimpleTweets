package com.codepath.apps.simpletweets;

import android.app.Application;
import android.content.Context;

import com.codepath.apps.simpletweets.models.User;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends Application {
    private static Context context;

    private static User accountUser;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        FlowManager.init(new FlowConfig.Builder(this).build());
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

        TwitterApplication.context = this;

    }

    public static TwitterClient getRestClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
    }


    public static void setAccountUser(User user) {
        accountUser = user;
    }

    public static User getAccountUser() {
        return accountUser;
    }
}