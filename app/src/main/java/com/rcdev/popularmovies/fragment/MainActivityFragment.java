package com.rcdev.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.rcdev.popularmovies.activity.SettingsActivity;
import com.rcdev.popularmovies.data.MovieContract;
import com.rcdev.popularmovies.data.MovieDBHelper;
import com.rcdev.popularmovies.utils.Constants;
import com.rcdev.popularmovies.objects.MovieItem;
import com.rcdev.popularmovies.adapter.MoviePosterAdapter;
import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.activity.DetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private MoviePosterAdapter mMovieAdapter;
    private ArrayList<MovieItem> mMovieData;
    private MovieItem[] mMovies;
    @Bind(R.id.gridView)
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMovieData = new ArrayList<>();

        } else {
            mMovieData = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    public MainActivityFragment() {
    }

    public interface Callback {

        /**
         * Called when an item is selected in the gridview
         *
         * @param movie the selected movie
         */
        void onItemSelected(MovieItem movie);
    }


    @Override
    public void onStart() {
        super.onStart();
        requestMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMovieData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mMovieAdapter = new MoviePosterAdapter(getActivity(), R.layout.movie_grid_item, mMovieData);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final MovieItem movie = mMovieAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });


        if (savedInstanceState == null) {
            requestMovies();
        } else {
            mMovies = (MovieItem[]) savedInstanceState.getParcelableArray("movies");
            if (mMovies != null) {
                mMovieAdapter.clear();
                for (final MovieItem movie : mMovies) {
                    mMovieAdapter.add(movie);
                }
                mMovieAdapter.notifyDataSetChanged();
            }
        }


        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_YEAR,
            MovieContract.MovieEntry.COLUMN_RATING,};

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


    private boolean requestMovies() {
        if (isNetworkAvailable()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String direction = prefs.getString(getString(R.string.pref_direction_key), getString(R.string.pref_direction_default));
            if ("favorites".equals(direction)) {
                MovieDBHelper db = new MovieDBHelper(getContext());
                ArrayList<MovieItem> favorites = db.getAllMovies();
                if (favorites.size() == 0) {
                    Toast.makeText(getActivity(), "No Favorites Added", Toast.LENGTH_SHORT).show();
                }
                mMovieAdapter = new MoviePosterAdapter(getActivity(), R.layout.movie_grid_item, favorites);
                gridView.setAdapter(mMovieAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view,
                                            final int position, final long id) {
                        MovieItem movie = mMovieAdapter.getItem(position);
                        ((Callback) getActivity()).onItemSelected(movie);
                    }
                });


            } else {
                GetMoviesFromURL getMovies = new GetMoviesFromURL();
                getMovies.execute(direction);
                return true;
            }
        } else {
            Toast toast = Toast.makeText(getActivity(), "Network Not Available", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return false;
    }


    public class GetMoviesFromURL extends AsyncTask<String, Void, ArrayList<MovieItem>> {
        private final String LOG_TAG = GetMoviesFromURL.class.getSimpleName();

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieData = null;
            try {
                final String apiKey = "api_key";
                final String sortKey = "sort_by";
                Uri buildUri = Uri.parse(Constants.BASE_REQ_PATH).buildUpon()
                        .appendQueryParameter(sortKey, params[0])
                        .appendQueryParameter(apiKey, Constants.API_KEY)
                        .build();
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {

                    return null;
                }
                movieData = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Closing stream", e);
                    }
                }
            }

            try {
                return getMovieData(movieData);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        private ArrayList<MovieItem> getMovieData(String movieData) throws JSONException {
            JSONObject movieObject = new JSONObject(movieData);
            JSONArray movieArray = movieObject.getJSONArray(Constants.RESULTS);
            ArrayList<MovieItem> movieCollection = new ArrayList<>();
            for (int i = 0; i < movieArray.length(); i++) {
                MovieItem moviePoster = new MovieItem();
                JSONObject movieResults = movieArray.getJSONObject(i);
                moviePoster.setId(movieResults.getString(Constants.ID));
                moviePoster.setTitle(movieResults.getString(Constants.TITLE));
                moviePoster.setPath(movieResults.getString(Constants.POSTER_PATH));
                moviePoster.setOverview(movieResults.getString(Constants.DESC));
                moviePoster.setPopularity(movieResults.getString(Constants.POPULARITY));
                moviePoster.setRelease_date(movieResults.getString(Constants.RELEASE_DATE));
                moviePoster.setVote_average(movieResults.getString(Constants.VOTE_AVERAGE));
                moviePoster.setVote_count(movieResults.getString(Constants.VOTE_COUNT));
                moviePoster.setFull_poster(movieResults.getString(Constants.POSTER_PATH));
                movieCollection.add(moviePoster);
            }
            return movieCollection;
        }

        public GetMoviesFromURL() {
            super();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movies) {
            super.onPostExecute(movies);

            if (movies != null) {
                mMovieAdapter.clear();
                for (MovieItem item : movies) {
                    mMovieAdapter.add(item);
                }
                mMovieAdapter.notifyDataSetChanged();
            }

        }
    }

}



