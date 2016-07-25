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
public class Movie implements Parcelable {

    private static final String TAG = Movie.class.getSimpleName();

    private final String backdropPath;
    private final String posterPath;
    private final String originalTitle;
    private final String overView;
    private final String releaseDate;
    private final double voteAverage;
    private final int id;
    private final String PREFIX_POSTER_URL = "https://image.tmdb.org/t/p/w500/%s";

    public Movie(JSONObject jsonObject) throws JSONException {
        this.posterPath = jsonObject.getString("poster_path");
        this.originalTitle = jsonObject.getString("original_title");
        this.overView = jsonObject.getString("overview");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.voteAverage = jsonObject.getDouble("vote_average");
        this.id = jsonObject.getInt("id");
        this.releaseDate = jsonObject.getString("release_date");
    }


    public static ArrayList<Movie> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Movie> moviesList = new ArrayList<>();

        if(jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    moviesList.add(new Movie(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    Log.e(TAG, "fromJsonArray: error while parsing the movie object at position: " + i, e);
                }
            }
        }
        return moviesList;
    }

    public String getPosterPath() {
        return String.format(PREFIX_POSTER_URL, posterPath);
    }

    public String getBannerPoster() {
        return String.format(PREFIX_POSTER_URL, backdropPath);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverView() {
        return overView;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getId() {
        return id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.backdropPath);
        dest.writeString(this.posterPath);
        dest.writeString(this.originalTitle);
        dest.writeString(this.overView);
        dest.writeDouble(this.voteAverage);
        dest.writeInt(this.id);
        dest.writeString(this.releaseDate);
    }

    protected Movie(Parcel in) {
        this.backdropPath = in.readString();
        this.posterPath = in.readString();
        this.originalTitle = in.readString();
        this.overView = in.readString();
        this.voteAverage = in.readDouble();
        this.id = in.readInt();
        this.releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
