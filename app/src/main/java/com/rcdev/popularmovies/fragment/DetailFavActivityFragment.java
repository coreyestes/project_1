package com.rcdev.popularmovies.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.adapter.ReviewAdapter;
import com.rcdev.popularmovies.adapter.TrailerAdapter;
import com.rcdev.popularmovies.data.MovieDBHelper;
import com.rcdev.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by coreyestes on 4/8/16.
 */
public class DetailFavActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailFavActivityFragment.class.getSimpleName();
    protected String movie_id;
    protected String movie_title;
    protected String movie_poster;
    protected String movie_release;
    protected String movie_rating;
    protected String movie_overview;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_favorite_detail, container, false);
        Intent intent = getActivity().getIntent();


        //pass ID
        if (intent != null && intent.hasExtra(Constants.ID)) {
            movie_id = intent.getStringExtra(Constants.ID);
            Log.i(LOG_TAG, "movie id is: " + movie_id);
        }

        //pass title
        if (intent != null && intent.hasExtra(Constants.TITLE)) {
            movie_title = intent.getStringExtra(Constants.TITLE);
            ((TextView) detailView.findViewById(R.id.tvTitle))
                    .setText(movie_title);

            getActivity().setTitle(movie_title);

            movie_poster = intent.getStringExtra("path");
            Log.i(LOG_TAG, "poster URL: " + movie_poster);
            ImageView poster = (ImageView) detailView.findViewById(R.id.ivPoster);
            Picasso
                    .with(getActivity())
                    .load(movie_poster)
                    .fit()
                    .into(poster);


            //pass release date
            movie_release = intent.getStringExtra(Constants.RELEASE_DATE);
            ((TextView) detailView.findViewById(R.id.tvReleaseDate))
                    .setText(movie_release);
            //pass rating
            movie_rating = intent.getStringExtra("vote_avg");
            ((TextView) detailView.findViewById(R.id.movie_rating_text))
                    .setText(movie_rating);
            //pass overview
            movie_overview = intent.getStringExtra(Constants.DESC);
            ((TextView) detailView.findViewById(R.id.tvOverview))
                    .setText(movie_overview);


        }
        return detailView;
    }







}
