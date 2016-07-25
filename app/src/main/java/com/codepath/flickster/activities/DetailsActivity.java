package com.codepath.flickster.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.flickster.R;
import com.codepath.flickster.model.Movie;
import com.codepath.flickster.model.Video;
import com.codepath.flickster.network.NetworkManager;
import com.codepath.flickster.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Handles displaying full description, rating, playing trailer capability
 * and share capability.
 *
 * @author  Yogesh Shrivastava
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = TAG + ".EXTRA_MOVIE";
    private static final String YOUTUBE_LINK = "https://www.youtube.com/watch?v=%1$s";
    private Movie movie;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.titleTextView)
    TextView title;

    @BindView(R.id.overviewTextView)
    TextView overview;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.releaseDate)
    TextView releaseDate;

    @BindView(R.id.movieImage)
    ImageView bannerImage;

    @BindView(R.id.playTrailerImage)
    ImageView play;

    @BindView(R.id.playTrailerFab)
    FloatingActionButton playTrailerFab;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EXPANDED, COLLAPSED, IDLE})
    private @interface State{}

    private final static int EXPANDED = 0;
    private final static int COLLAPSED = 1;
    private final static int IDLE = 2;

    private @State int mCurrentState = IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        if(savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(EXTRA_MOVIE);
        } else {
            if(getIntent() != null && getIntent().hasExtra(EXTRA_MOVIE)) {
                movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            }
        }
        initToolbar();
        populateMovieDetails();
    }

    private void populateMovieDetails() {
        ratingBar.setRating((float)movie.getVoteAverage()/2);
        title.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverView());
        releaseDate.setText(UiUtils.getFormattedDate(movie.getReleaseDate()));
        fetchBannerMovieImage(bannerImage, play, movie);
    }

    /**
     * Fetching banner image for a given movie.
     *
     * @param movieImage
     * @param playButton
     * @param movie
     */
    private void fetchBannerMovieImage(final ImageView movieImage, final ImageView playButton, Movie movie) {
        playButton.setVisibility(View.GONE);
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                movieImage.setBackground(null);
                playButton.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError() {}
        };

        Picasso.with(this).load(movie.getBannerPoster()).placeholder(R.drawable.poster_banner).into(movieImage, callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_MOVIE, movie);
    }

    private void initToolbar() {
        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Trick to show title only when collapsed for transition.
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

        // Collapsible toolbar callback hack :)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0) {
                    if (mCurrentState != EXPANDED) {
                        // Expanded
                        collapsingToolbar.setTitle("");
                        title.setVisibility(View.VISIBLE);
                        playTrailerFab.hide();
                    }
                    mCurrentState = EXPANDED;
                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    if (mCurrentState != COLLAPSED) {
                        // Collapsed
                        collapsingToolbar.setTitle(movie.getOriginalTitle());
                        title.setVisibility(View.INVISIBLE);
                        playTrailerFab.show();
                    }
                    mCurrentState = COLLAPSED;
                } else {
                    if (mCurrentState != IDLE) {
                        // IDLE
                    }
                    mCurrentState = IDLE;
                }
            }
        });
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.playTrailerImage, R.id.playTrailerFab})
    public  void onPlayTrailerClicked() {
        // Trigger youtube video if it's a bannerImage type.
        Intent intent = new Intent(DetailsActivity.this, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO_ID, movie.getId());
        ActivityCompat.startActivity(DetailsActivity.this, intent, null);
    }

    @OnClick(R.id.share)
    public void onShareClicked() {
        // Make a network call
        NetworkManager networkManager = NetworkManager.getNetworkManager();
        networkManager.getVideoLink(movie.getId(), new NetworkManager.VideoRequestCallback() {
            @Override
            public void onSuccess(ArrayList<Video> videoList) {
                if(videoList.size() > 0) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_check_trailer) + movie.getOriginalTitle());
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_trailer_title) + String.format(YOUTUBE_LINK, videoList.get(0).getKey()) + getString(R.string.share_overview_title) + movie.getOverView());
                    startActivity(Intent.createChooser(share, getString(R.string.share_title)));
                }
            }
            @Override
            public void onFailure() {
                Toast.makeText(DetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
