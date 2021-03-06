package com.codepath.apps.simpletweets.models;

import com.codepath.apps.simpletweets.db.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Table(database = TwitterDatabase.class)
@Parcel(analyze = {Tweet.class})
public class Tweet extends BaseModel {

    @Column
    @PrimaryKey
    long uid;

    @Column
    String body;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    @Column
    String createAt;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Media media;

    @Column
    boolean isMention;


    public String getBody() {
        return body;
    }

    public String getCreateAt() {
        return createAt;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public boolean isMention() {
        return isMention;
    }

    public static Tweet fromJSON(JSONObject jsonObject, boolean isMention) {
        Tweet tweet = new Tweet();
        try {
            tweet.isMention = isMention;
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            JSONObject entries = jsonObject.getJSONObject("entities");
            if (entries.optJSONArray("media") != null) {
                tweet.media = Media.fromJSON(entries.getJSONArray("media").getJSONObject(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        return fromJSONArray(jsonArray, false);
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray, boolean isMention) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i), isMention);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweets;
    }

    public static List<Tweet> getUniqueTweets(List<Tweet> oldTweets, List<Tweet> newTweets) {
        if (oldTweets.isEmpty()) {
            return newTweets;
        }

        List<Tweet> res = new ArrayList<>();

        for (Tweet tweet : newTweets) {
            if (!oldTweets.contains(tweet)) {
                res.add(tweet);
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        return uid == tweet.uid;

    }

    @Override
    public int hashCode() {
        return (int) (uid ^ (uid >>> 32));
    }
}
