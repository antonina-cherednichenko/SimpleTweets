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
@Parcel(analyze = {Media.class})
public class Media extends BaseModel {

    @Column
    @PrimaryKey
    long uid;

    @Column
    String mediaUrl;

    @Column
    String type;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getType() {
        return type;
    }

    public long getUid() {
        return uid;
    }

    public static Media fromJSON(JSONObject jsonObject) {
        Media media = new Media();

        try {
            media.uid = jsonObject.getLong("id");
            media.type = jsonObject.getString("type");
            media.mediaUrl = jsonObject.getString("media_url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        return uid == media.uid;

    }

    @Override
    public int hashCode() {
        return (int) (uid ^ (uid >>> 32));
    }
}
