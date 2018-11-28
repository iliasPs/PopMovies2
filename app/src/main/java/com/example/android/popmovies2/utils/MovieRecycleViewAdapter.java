package com.example.android.popmovies2.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies2.MovieDetailActivity;
import com.example.android.popmovies2.R;
import com.example.android.popmovies2.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieRecycleViewAdapter extends RecyclerView.Adapter<MovieRecycleViewAdapter.MoviesViewHolder>  {

    private List<Movie> mMovies;



    private Context mContext;


    public MovieRecycleViewAdapter(Context c, List<Movie> myMovieData) {
        mContext = c;
        mMovies =myMovieData;
    }

    @NonNull
    @Override
    public MovieRecycleViewAdapter.MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MoviesViewHolder viewHolder = new MoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecycleViewAdapter.MoviesViewHolder holder, int position) {
        Movie movie = this.mMovies.get(position);
        Picasso.get()
                .load(movie.getMoviePoster())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.moviePosterIv);
        holder.movieTitleTv.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != mMovies ? mMovies.size() : 0);
    }

    public List<Movie> getMovieList() { return this.mMovies; }


    /**
     * Cache of the children views for a list item.
     */
   public class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView moviePosterIv;
        TextView movieTitleTv;

        public MoviesViewHolder(View itemView) {
            super(itemView);

            moviePosterIv = itemView.findViewById(R.id.moviePoster);
            movieTitleTv = itemView.findViewById(R.id.movieTitleListItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            int clickedPosition = getAdapterPosition();
            Intent i = new Intent(context, MovieDetailActivity.class);
           Movie movie = mMovies.get(clickedPosition);
            i.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
            context.startActivity(i);



        }
    }


    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setMovies(List<Movie> movieEntries) {
        mMovies = movieEntries;
        notifyDataSetChanged();
    }
}
