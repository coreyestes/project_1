package com.rcdev.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView overviewTextView;
    private TextView releaseDateTextView;
    private TextView popularityTextView;
    private TextView voteAverageTextView;
    private TextView voteCntTextView;
    private TextView fullPathTextView;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        String title = bundle.getString("title");
        String overview = bundle.getString("overview");
        String release = bundle.getString("release_date");
        String popularity = bundle.getString("popularity");
        Double voteAverage = bundle.getDouble("vote_avg");
        String voteCnt = bundle.getString("vote_cnt");
        String path = bundle.getString("path");


        titleTextView = (TextView) findViewById(R.id.tvTitle);
        titleTextView.setText(title);

        overviewTextView = (TextView) findViewById(R.id.tvOverview);
        overviewTextView.setText(overview);

        releaseDateTextView = (TextView) findViewById(R.id.tvReleaseDate);
        releaseDateTextView.setText(release.substring(0,4));

        voteAverageTextView = (TextView) findViewById(R.id.tvVoteAvg);
        voteAverageTextView.setText((voteAverage).toString()+" / 10");

        poster = (ImageView) findViewById(R.id.ivDetailPoster);

        Picasso.with(this)
                .load(path)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(poster);

    }

    public class GetTrailersFromID extends AsyncTask<Void, Void, Void > {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else {
            onBackPressed();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
