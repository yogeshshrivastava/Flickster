package com.codepath.flickster.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author yvastavaus
 */
public class Video implements Parcelable {

    private static final String TAG = Video.class.getSimpleName();

    private final String key;

    public Video(JSONObject jsonObject) throws JSONException {
        this.key = jsonObject.getString("key");
    }

    public static ArrayList<Video> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Video> moviesList = new ArrayList<>();

        if(jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    moviesList.add(new Video(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    Log.e(TAG, "fromJsonArray: error while parsing the video object at position: " + i, e);
                }
            }
        }
        return moviesList;
    }


    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
    }

    protected Video(Parcel in) {
        this.key = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
