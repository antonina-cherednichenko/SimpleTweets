package com.codepath.apps.simpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1/";
    public static final String REST_CONSUMER_KEY = "92zmefHrbwsVhmhDOxRFIOcMi";
    public static final String REST_CONSUMER_SECRET = "lvB8CzkSCqexJ5KuXgsVjJjknDiJMQTIz0KImF3U2YiaVPgPnc";
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    public static final int TWEETS_COUNT = 50;
    public static final int MENTIONS_COUNT = 50;
    public static final int USER_TWEETS_COUNT = 50;

    private Context context;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);

        this.context = context;
    }


    //HomeTimeline - Gets the home timeline
    public void getHomeTimeline(Long maxId, Long sinceId, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }

        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_COUNT);
        if (sinceId != null) {
            params.put("since_id", sinceId);
        }

        if (maxId != null) {
            params.put("max_id", maxId);
        }
        //Execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getMentions(Long maxId, Long sinceId, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }

        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("count", MENTIONS_COUNT);
        if (sinceId != null) {
            params.put("since_id", sinceId);
        }

        if (maxId != null) {
            params.put("max_id", maxId);
        }
        //Execute the request
        getClient().get(apiUrl, params, handler);

    }

    public void getUserTimeline(Long uid, String screenName, Long maxId, Long sinceId, JsonHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }

        String apiUrl = getApiUrl("statuses/user_timeline.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("count", USER_TWEETS_COUNT);

        if (sinceId != null) {
            params.put("since_id", sinceId);
        }

        if (maxId != null) {
            params.put("max_id", maxId);
        }

        params.put("user_id", uid);
        params.put("screen_name", screenName);
        //Execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }
        String apiUrl = getApiUrl("account/verify_credentials.json");
        //Specify the parameters
        RequestParams params = new RequestParams();

        getClient().get(apiUrl, params, handler);

    }

    public void addNewTweet(String tweetBody, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }
        String apiUrl = getApiUrl("statuses/update.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);

        getClient().post(apiUrl, params, handler);

    }


    public void getSearchTweets(String query, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }
        String apiUrl = getApiUrl("search/tweets.json");

        RequestParams params = new RequestParams();
        params.put("q", query);

        getClient().get(apiUrl, params, handler);

    }

    public void retweetTweet(long tweetId, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }
        String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", tweetId));

        //Specify the parameters
        RequestParams params = new RequestParams();

        getClient().post(apiUrl, params, handler);

    }

    public void replyTweet(long tweetId, String reply, AsyncHttpResponseHandler handler) {
        if (!isOnline() || !isNetworkAvailable()) {
            return;
        }
        String apiUrl = getApiUrl("statuses/update.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("status", reply);
        params.put("in_reply_to_status_id", tweetId);

        getClient().post(apiUrl, params, handler);

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


}
