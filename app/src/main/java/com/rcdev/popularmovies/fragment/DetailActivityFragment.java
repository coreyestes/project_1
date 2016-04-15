package com.rcdev.popularmovies.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.adapter.ReviewAdapter;
import com.rcdev.popularmovies.adapter.TrailerAdapter;
import com.rcdev.popularmovies.data.MovieContract;
import com.rcdev.popularmovies.data.MovieDBHelper;
import com.rcdev.popularmovies.objects.MovieItem;
import com.rcdev.popularmovies.objects.ReviewItem;
import com.rcdev.popularmovies.objects.TrailerItem;
import com.rcdev.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

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

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String MKEY = "movie";

    protected MovieItem mMovie;
    protected String movie_id;
    protected String movie_title;
    protected String movie_poster;
    protected String movie_release;
    protected String movie_rating;
    protected String movie_overview;

    private Context mContext;
    private ArrayList<TrailerItem> mTrailerData;
    private ArrayList<ReviewItem> mReviewData;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    public static DetailActivityFragment newInstance(MovieItem movie) {
        final DetailActivityFragment fragment = new DetailActivityFragment();
        final Bundle args = new Bundle();
        args.putParcelable("movie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailActivityFragment() {
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers", mTrailerData);
        outState.putParcelableArrayList("reviews", mReviewData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("trailers") || !savedInstanceState.containsKey("reviews")) {
            mTrailerData = new ArrayList<>();
            mReviewData = new ArrayList<>();

        } else {
            mTrailerData = savedInstanceState.getParcelableArrayList("trailers");
            mReviewData = savedInstanceState.getParcelableArrayList("reviews");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle args = getArguments();
        mMovie = args.getParcelable("movie");


        assert mMovie != null;
        movie_id = mMovie.getId();

        movie_title = mMovie.getTitle();
        ((TextView) detailView.findViewById(R.id.tvTitle))
                .setText(movie_title);
        getActivity().setTitle(movie_title);
        movie_poster = mMovie.getPath();
        ImageView poster = (ImageView) detailView.findViewById(R.id.ivPoster);
        Picasso
                .with(getActivity())
                .load(movie_poster)
                .fit()
                .into(poster);


        //pass release date
        movie_release = mMovie.getRelease_date();
        ((TextView) detailView.findViewById(R.id.tvReleaseDate))
                .setText(movie_release);
        //pass rating
        movie_rating =  mMovie.getVote_average();
        ((TextView) detailView.findViewById(R.id.movie_rating_text))
                .setText(movie_rating);
        //pass overview
        movie_overview = mMovie.getOverview();
        ((TextView) detailView.findViewById(R.id.tvOverview))
                .setText(movie_overview);

        ListView reviewListView = (ListView) detailView.findViewById(R.id.lvReview);
        mReviewAdapter = new ReviewAdapter(getContext(), R.layout.review_item, mReviewData);
        reviewListView.setAdapter(mReviewAdapter);

        ListView trailerListView = (ListView) detailView.findViewById(R.id.lvTrailer);
        mTrailerAdapter = new TrailerAdapter(getContext(), R.layout.trailer_item, mTrailerData);
        trailerListView.setAdapter(mTrailerAdapter);

        FloatingActionButton fab = (FloatingActionButton) detailView.findViewById(R.id.btFavorite);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDBHelper db = new MovieDBHelper(getContext());

                if (!db.hasObject(movie_id)) {
                    saveToDatabase();
                } else {
                    removeDatabase();
                }
            }
        });

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerData.get(position).getKey())));
            }
        });


        return detailView;
    }


    @Override
    public void onStart() {
        super.onStart();
        //Execute FetchTrailerTask
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();

        //Execute FetchReviewTask
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();
    }

    protected void saveToDatabase() {
        MovieDBHelper db = new MovieDBHelper(getContext());
        ContentValues contentValues = generateContentValues();
        db.getWritableDatabase().insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        Toast.makeText(getContext(), R.string.addDB, Toast.LENGTH_SHORT).show();
    }

    protected void removeDatabase() {
        MovieDBHelper db = new MovieDBHelper(getContext());
        db.getWritableDatabase().delete(MovieContract.MovieEntry.TABLE_NAME, "movie_id = ?", new String[]{String.valueOf(movie_id)});
        Toast.makeText(getContext(), R.string.removeDB, Toast.LENGTH_SHORT).show();
    }

    private ContentValues generateContentValues() {
        ContentValues favoriteMovieValues = new ContentValues();
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie_title);
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie_poster);
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, movie_overview);
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_YEAR, movie_release);
        favoriteMovieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, movie_rating);
        return favoriteMovieValues;
    }


    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<ReviewItem>> {

        private ArrayList<ReviewItem> getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            JSONObject reviewObject = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewObject.getJSONArray(Constants.RESULTS);
            ArrayList<ReviewItem> reviewCollection = new ArrayList<>();
            for (int i = 0; i < reviewArray.length(); i++) {
                ReviewItem reviewItem = new ReviewItem();
                JSONObject review = reviewArray.getJSONObject(i);
                reviewItem.setAuthor(review.getString("author"));
                reviewItem.setContent(review.getString("content"));
                reviewCollection.add(reviewItem);
            }
            return reviewCollection;
        }

        @Override
        protected ArrayList<ReviewItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewDataStr = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews";
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .build();

                URL url = new URL(buildUri.toString());

                //Create the request to Moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Reading input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    //empty stream. No parsing
                    return null;
                }
                reviewDataStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getReviewDataFromJson(reviewDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewItem> result) {
            if (result != null && !result.isEmpty()) {
                mReviewAdapter.clear();
                for (ReviewItem review : result) {
                    mReviewAdapter.add(review);
                }
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<TrailerItem>> {

        private ArrayList<TrailerItem> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            JSONObject trailerObject = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerObject.getJSONArray(Constants.RESULTS);
            ArrayList<TrailerItem> trailerCollection = new ArrayList<>();
            for (int i = 0; i < trailerArray.length(); i++) {
                final String youtubeUrl = "https://www.youtube.com/watch?v=";
                TrailerItem trailerItem = new TrailerItem();
                JSONObject trailerResults = trailerArray.getJSONObject(i);
                trailerItem.setKey(youtubeUrl + trailerResults.getString(Constants.KEY));
                trailerItem.setName(trailerResults.getString(Constants.NAME));
                trailerCollection.add(trailerItem);

            }
            return trailerCollection;
        }

        @Override
        protected ArrayList<TrailerItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // contain the raw JSON response as as string
            String trailerDataStr = null;
            try {
                //construct URL
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .build();
                URL url = new URL(buildUri.toString());

                //Create the request to Moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Reading input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    //empty stream. No parsing
                    return null;
                }
                trailerDataStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getTrailerDataFromJson(trailerDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TrailerItem> result) {
            if (result != null && !result.isEmpty()) {
                mTrailerAdapter.clear();
                for (TrailerItem trailer : result) {
                    mTrailerAdapter.add(trailer);
                }
                mTrailerAdapter.notifyDataSetChanged();
            }
        }
    }
}





