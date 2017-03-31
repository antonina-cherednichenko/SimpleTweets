package com.codepath.apps.simpletweets.models;


import com.codepath.apps.simpletweets.db.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(database = TwitterDatabase.class)
@Parcel(analyze = {User.class})
public class User extends BaseModel {
    @Column
    @PrimaryKey
    long uid;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileUrl;

    @Column
    String tagline;

    @Column
    int followers;

    @Column
    int following;

    @Column
    String profileBannerUrl;

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUid() {
        return uid;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public String getTagline() {
        return tagline;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public static User fromJSON(JSONObject json) {
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileUrl = json.getString("profile_image_url_https");
            user.followers = json.getInt("followers_count");
            user.following = json.getInt("friends_count");
            user.tagline = json.getString("description");
            user.profileBannerUrl = json.getString("profile_banner_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
