package com.rcdev.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by coreyestes on 7/22/15.
 */
public class MoviePosterAdapter extends ArrayAdapter<MovieItem> {


    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MovieItem> mMovies = new ArrayList<MovieItem>();


    public MoviePosterAdapter(Context mContext, int layoutResourceId, ArrayList<MovieItem> mMovies) {
        super(mContext, layoutResourceId, mMovies);
        this.mContext = mContext;
        this.mMovies = mMovies;
        this.layoutResourceId = layoutResourceId;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;
        String url = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivPoster = (ImageView) row.findViewById(R.id.ivMovieItem);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        MovieItem item = mMovies.get(position);
        url = item.getFull_poster();
        item.setPath(url);
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.ivPoster);

        return row;
    }


    private class ViewHolder {
        ImageView ivPoster;
    }


}
