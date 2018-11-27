package com.example.android.popmovies2.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies2.R;
import com.example.android.popmovies2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieTrailersRecyclerViewAdapter extends RecyclerView.Adapter<MovieTrailersRecyclerViewAdapter.TrailersViewHolder> {


    private List<Trailer> mTrailers;
    private Context mContext;
    private static final String LOG_TAG = MovieTrailersRecyclerViewAdapter.class.getSimpleName();

    public MovieTrailersRecyclerViewAdapter(Context c, List<Trailer> mTrailers){
        this.mTrailers= mTrailers;
        mContext = c;
    }

    @NonNull
    @Override
    public MovieTrailersRecyclerViewAdapter.TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailersViewHolder viewHolder = new TrailersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersRecyclerViewAdapter.TrailersViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        holder.trailerTitle.setText(trailer.getTrailerName());
        // Show movie poster image
        String thumbnailPath = "http://img.youtube.com/vi/" + trailer.getTrailerKey() + "/0.jpg";

        Picasso.get()
                .load(thumbnailPath)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.trailerThumb);
    }

    @Override
    public int getItemCount() {
       return  (null != mTrailers ? mTrailers.size() : 0);
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.trailer_title) TextView trailerTitle;
        @BindView(R.id.trailer_thumb) AppCompatImageView trailerThumb;
        public TrailersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();
            Trailer trailer = mTrailers.get(position);
            try{
                PopMoviesUtils.launchTrailerVideoInYoutubeApp(context,trailer.getTrailerKey());
            }catch (ActivityNotFoundException exception){
                PopMoviesUtils.launchTrailerVideoInYoutubeBrowser(context, trailer.getTrailerKey());
            }
        }
    }
}
