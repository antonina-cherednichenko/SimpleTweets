package com.codepath.apps.simpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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
    public static final String REST_CONSUMER_KEY = "3SY8ZDLBJ4yHjXRQIwqrqQQyi";
    public static final String REST_CONSUMER_SECRET = "52aI4mBmPdEgKOimP2TYAXakt16cs8zX1NeG8CI28poCor9zoP";
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    public static final int TWEETS_COUNT = 25;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }


    //HomeTimeline - Gets the home timeline
    public void getHomeTimeline(Long maxId, Long sinceId, AsyncHttpResponseHandler handler) {
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

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        //Specify the parameters
        RequestParams params = new RequestParams();

        getClient().get(apiUrl, params, handler);

    }

    public void addNewTweet(String tweetBody, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        //Specify the parameters
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);

        getClient().post(apiUrl, params, handler);

    }



	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
