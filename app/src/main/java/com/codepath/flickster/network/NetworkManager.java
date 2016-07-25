package com.codepath.flickster.network;

import android.util.Log;

import com.codepath.flickster.model.Movie;
import com.codepath.flickster.model.Video;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Handles all the network calls with the Movie API
 *
 * @author yvastavaus.
 */
public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();
    private static NetworkManager INSTANCE;

    private AsyncHttpClient mClient;
    private static final String NOW_SHOWING_MOVIES = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static final String VIDEO_API = "https://api.themoviedb.org/3/movie/%1$s/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static final String TRAILER_API = "https://api.themoviedb.org/3/movie/%1$d/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    /**
     * Interface to handle response from the Movies for Now Showing Database.
     */
    public interface RequestCallback {
        void onSuccess(ArrayList<Movie> moviesList);
        void onFailure();
    }


    /**
     * Interface to handle response from the Video API for Now Showing Database.
     */
    public interface VideoRequestCallback {
        void onSuccess(ArrayList<Video> videoList);
        void onFailure();
    }

    private NetworkManager() {
        this.mClient = new AsyncHttpClient();
    }

    public static NetworkManager getNetworkManager() {
        if(INSTANCE == null) {
            INSTANCE = new NetworkManager();
        }

        return INSTANCE;
    }

    public void getNowShowingMovies(final RequestCallback callback) {
        Log.d(TAG, "getNowShowingMovies: url: " + NOW_SHOWING_MOVIES);
        mClient.get(NOW_SHOWING_MOVIES, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Movie> movieList = Movie.fromJsonArray(response.getJSONArray("results"));
                    callback.onSuccess(movieList);
                } catch (JSONException e) {
                    Log.e(TAG, "getNowShowingMovies:: error while parsing movie results");
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure();
            }
        });
    }

    public void getVideoLink(int id, final VideoRequestCallback callback) {
        String url = String.format(VIDEO_API, id);
        mClient.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Video> movieList = Video.fromJsonArray(response.getJSONArray("results"));
                    callback.onSuccess(movieList);
                } catch (JSONException e) {
                    Log.e(TAG, "getNowShowingMovies:: error while parsing movie results");
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure();
            }
        });

    }
}
