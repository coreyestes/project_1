package com.rcdev.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.fragment.DetailActivityFragment;
import com.rcdev.popularmovies.fragment.MainActivityFragment;
import com.rcdev.popularmovies.objects.MovieItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {


    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.content_detail) != null;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onItemSelected(MovieItem movie) {

        if (mTwoPane) {
            final DetailActivityFragment fragment = DetailActivityFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_detail, fragment, DetailActivityFragment.class.getSimpleName())
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        }
    }
}
