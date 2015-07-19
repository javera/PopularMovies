package com.mjaworski1988.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadline();
    }

    /**
     * Used to set the sort order in the action bar
     */
    private void setHeadline() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // get the sort order from app settings (default to 'most popular')
        String sortOrderFromPrefs = sharedPref.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity));

        // set the header accordingly
        if (sortOrderFromPrefs.contentEquals(getString(R.string.pref_sort_order_popularity))) {
            setTitle(R.string.header_movie_by_popularity);
        } else {
            setTitle(R.string.header_movie_by_ranking);
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
