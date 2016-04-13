package com.rcdev.popularmovies.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.rcdev.popularmovies.R;

import com.rcdev.popularmovies.activity.FavoriteActivity;
import com.rcdev.popularmovies.adapter.MoviePosterAdapter;
import com.rcdev.popularmovies.data.MovieContract;
import com.rcdev.popularmovies.data.MovieDBHelper;
import com.rcdev.popularmovies.objects.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.AdapterView.*;

/**
 * Created by corey on 3/28/16.
 */
public class FavoriteActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = FavoriteActivity.class.getSimpleName();
    private MoviePosterAdapter mMovieAdapter;
    private ArrayList<MovieItem> mMovieData;

    @Bind(R.id.favGridView)
    GridView gridView;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_YEAR,
            MovieContract.MovieEntry.COLUMN_RATING,};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, rootView);
        MovieDBHelper db = new MovieDBHelper(getContext());
        ArrayList<MovieItem> favorites = db.getAllMovies();
        if (favorites.size() < 0) {
            Toast.makeText(getActivity(), "No Favorites Added", Toast.LENGTH_SHORT).show();
        }

        mMovieAdapter = new MoviePosterAdapter(getActivity(), R.layout.movie_grid_item, favorites);
        gridView.setAdapter(mMovieAdapter);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }
        MovieDBHelper db = new MovieDBHelper(getContext());
        db.getReadableDatabase();
        mMovieData = db.getAllMovies();
        db.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
