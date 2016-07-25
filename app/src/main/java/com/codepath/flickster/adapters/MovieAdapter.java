package com.codepath.flickster.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.flickster.R;
import com.codepath.flickster.model.Movie;
import com.codepath.flickster.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Handles the items displayed in the recycler view for two types of items:
 *  1. Poster
 *  2. Banner
 *
 * @author yvastavaus.
 */
public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    // NOTE: Not using enums as they are memory intensive.

    // Type of items in the recycler view
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POSTER, BANNER})
    public @interface ItemType {}
    public static final int POSTER = 0;
    public static final int BANNER = 1;

    private final ArrayList<Movie> movieList;
    private Context context;

    /**
     * Callback class when recycler view item is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, @MovieAdapter.ItemType int type);
    }

    /**
     * Callback when play button is clicked.
     *
     * NOTE: Currently this is different for Banner as I have
     * implemented extra for More information for popular items.
     */
    public interface OnPlayButtonListener {
        void onPlayClick(int position, @MovieAdapter.ItemType int type);
    }

    public MovieAdapter(Context context, ArrayList<Movie> movieList) {
        this.movieList = movieList;
        this.context = context;
    }

    private OnItemClickListener mListener;
    private OnPlayButtonListener mPlayListener;

    public void setPlayClickListener(OnPlayButtonListener listener) {
        this.mPlayListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case BANNER:
                View v1 = inflater.inflate(R.layout.item_movie_now_showing_banner, parent, false);
                viewHolder = new BannerViewHolder(v1, mListener, mPlayListener);
                break;
            case POSTER:
            default:
                View v2;
                    v2 = inflater.inflate(R.layout.item_movie_now_showing, parent, false);
                viewHolder = new PosterViewHolder(v2, mListener);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BANNER:
                BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
                configureViewBanner(bannerViewHolder, position);
                break;
            case POSTER:
            default:
                PosterViewHolder viewHolder = (PosterViewHolder) holder;
                configureViewPoster(viewHolder, position);
                break;
        }
    }

    private void configureViewPoster(PosterViewHolder posterViewHolder, int position) {
        Movie movie = movieList.get(position);
        posterViewHolder.title.setText(movie.getOriginalTitle());
        posterViewHolder.overview.setText(movie.getOverView());
        posterViewHolder.movieImage.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        fetchMovieImage(posterViewHolder.movieImage, movie);
    }

    private void configureViewBanner(BannerViewHolder bannerViewHolder, int position) {
        Movie movie = movieList.get(position);
        bannerViewHolder.title.setText(movie.getOriginalTitle());
        bannerViewHolder.movieImage.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        fetchBannerMovieImage(bannerViewHolder.movieImage, bannerViewHolder.playButtonImage, movie);
    }

    private void fetchBannerMovieImage(final ImageView movieImage, final ImageView playbutton, Movie movie) {
        playbutton.setVisibility(View.GONE);
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                movieImage.setBackground(null);
                playbutton.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError() {}
        };
        Picasso.with(context).load(movie.getBannerPoster()).placeholder(R.drawable.poster_banner).transform(new RoundedCornersTransformation(10, 10)).into(movieImage, callback);
    }


    @Override
    public int getItemViewType(int position) {
        if(movieList.get(position).getVoteAverage() > 5) {
            return BANNER;
        } else {
            return POSTER;
        }
    }

    /**
     * Fetch movie depending on the orientation of the device and applying image transformations.
     *
     * @param movieImage
     * @param movie
     */
    private void fetchMovieImage(final ImageView movieImage, Movie movie) {
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                movieImage.setBackground(null);
            }

            @Override
            public void onError() {

            }
        };

        if(UiUtils.isPortrait(context)) {
            Log.d(TAG, "onBindViewHolder: url: poster: " + movie.getPosterPath());
            Picasso.with(context).load(movie.getPosterPath()).transform(new RoundedCornersTransformation(10, 10)).placeholder(R.drawable.placeholder).into(movieImage, callback);
        } else {
            Log.d(TAG, "onBindViewHolder: url: Banner:  " + movie.getBannerPoster());
            Picasso.with(context).load(movie.getBannerPoster()).placeholder(R.drawable.poster_banner).transform(new RoundedCornersTransformation(10, 10)).into(movieImage, callback);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movieImage)
        public ImageView movieImage;

        @BindView(R.id.titleTextView)
        public TextView title;

        @BindView(R.id.overviewTextView)
        public TextView overview;

        OnItemClickListener mListener;

        public PosterViewHolder(View itemView, OnItemClickListener mListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = mListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getLayoutPosition(), POSTER);
            }
        }
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movieImage)
        ImageView movieImage;

        @BindView(R.id.titleTextView)
        TextView title;

        @BindView(R.id.playTrailerButton)
        ImageView playButtonImage;

        private final OnItemClickListener mListener;
        private final OnPlayButtonListener mPlayListener;

        public BannerViewHolder(View itemView, OnItemClickListener mListener, OnPlayButtonListener mPlayListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = mListener;
            this.mPlayListener = mPlayListener;
            itemView.setOnClickListener(this);
        }


        @SuppressWarnings("unused")
        @OnClick(R.id.playTrailerButton)
        public void onPlayTrailerButton() {
            if (mListener != null) {
                mPlayListener.onPlayClick(getLayoutPosition(), BANNER);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.more)
        public void onMoreButtonClicked(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getLayoutPosition(), BANNER);
            }
        }

        @Override
        public void onClick(View v) {
            // Future use.
        }
    }
}
