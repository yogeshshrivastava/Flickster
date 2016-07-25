package com.codepath.flickster.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codepath.flickster.R;
import com.codepath.flickster.adapters.MovieAdapter;
import com.codepath.flickster.model.Movie;
import com.codepath.flickster.network.NetworkManager;
import com.codepath.flickster.utils.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Launcher class that handles the home screen of the app.
 *
 * @author  Yogesh Shrivastava
 */
public class FlicksterHomeActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener, MovieAdapter.OnPlayButtonListener {

    private static final String EXTRA_MOVIE_LIST = FlicksterHomeActivity.class.getSimpleName() + ".EXTRA_MOVIE_LIST";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.movieRecyclerView)
    RecyclerView movieRecyclerView;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    private MovieAdapter adapter;
    private NetworkManager networkManager;
    private ArrayList<Movie> moviesList;

    final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initiateRequest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickster_home);
        ButterKnife.bind(this);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        boolean isFetchOnLoad = false;
        if(savedInstanceState != null) {
            moviesList = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_LIST);
        } else {
            moviesList = new ArrayList<>();
            isFetchOnLoad = true;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Setup Recycler view
        adapter = new MovieAdapter(this, moviesList);
        movieRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setPlayClickListener(this);
        movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        float offsetPx = getResources().getDimension(R.dimen.bottom_offset_dp);
        BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) offsetPx);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        movieRecyclerView.addItemDecoration(bottomOffsetDecoration);
        movieRecyclerView.addItemDecoration(itemDecoration);

        // Network manager
        networkManager = NetworkManager.getNetworkManager();

        // Swipe to refresh data.
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        // Only load data if necessary.
        if(isFetchOnLoad) {
            initiateRequest();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_MOVIE_LIST, moviesList);
    }

    /**
     * Queries backend for now showing movies and update the list accordingly.
     */
    private void initiateRequest() {
        swipeRefreshLayout.setRefreshing(true);
        networkManager.getNowShowingMovies(new NetworkManager.RequestCallback() {
            @Override
            public void onSuccess(ArrayList<Movie> list) {
                moviesList.addAll(list);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(View itemView, int position, @MovieAdapter.ItemType int type) {
        Intent intent;
        intent = new Intent(FlicksterHomeActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE, moviesList.get(position));
        ActivityCompat.startActivity(FlicksterHomeActivity.this, intent, null);
    }

    @Override
    public void onPlayClick(int position, @MovieAdapter.ItemType int type) {
        Intent intent;
        if(type == MovieAdapter.BANNER) {
            // Trigger youtube video if it's a bannerImage type.
            intent = new Intent(FlicksterHomeActivity.this, VideoActivity.class);
            intent.putExtra(VideoActivity.EXTRA_VIDEO_ID, moviesList.get(position).getId());
            ActivityCompat.startActivity(FlicksterHomeActivity.this, intent, null);
        }
    }

    /**
     * Handles divider for Recycler view between items.
     */
    private static class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mBottomOffset;

        public BottomOffsetDecoration(int bottomOffset) {
            mBottomOffset = bottomOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int dataSize = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (dataSize > 0 && position == dataSize - 1) {
                outRect.set(0, 0, 0, mBottomOffset);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }
    }
}
