package com.mjaworski1988.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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
import java.util.List;

/**
 * Used to display a grid of movie posters
 */
public class MovieGridFragment extends Fragment {

    // log constant
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private MovieGridAdapter mMovieAdapter;
    private String currentSortOrder;

    public MovieGridFragment() {
    }

    public void setCurrentSortOrder(String currentSortOrder) {
        this.currentSortOrder = currentSortOrder;
    }

    @Override
    public void onResume() {
        super.onResume();

        // if current sort order is different from the one in app settings, it means the user has just changed it,
        // need to refresh the list of films
        if (currentSortOrder != null && !currentSortOrder.equals(getSortOrderFromPreferences())) {
            Log.d(LOG_TAG, "Settings changed, refreshing task");
            refreshList();
        }
    }

    /**
     * Start async task to load movie posters and their details
     */
    private void refreshList() {
        Log.d(LOG_TAG, "Refreshing movies");
        new FetchMovieDataTask(mMovieAdapter).execute();
    }

    /**
     * Retrieve the sort order from app settings
     *
     * @return movie sort order, taken from app settings
     */
    private String getSortOrderFromPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPref.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity));
        return sortOrder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // get reference to the grid view, then set up the adapter
        GridView mMovieGrid = (GridView) rootView.findViewById(R.id.grid_movie_posters);
        mMovieAdapter = new MovieGridAdapter();
        mMovieGrid.setAdapter(mMovieAdapter);

        // start an async task to load movie posters
        refreshList();
        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieEntry touchedElement = (MovieEntry) parent.getItemAtPosition(position);
                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra(MovieEntry.EXTRA_MOVIE_ENTRY, touchedElement);
                startActivity(detailsIntent);
            }
        });

        return rootView;

    }

    /**
     * Retrieves movie data from TMDB, then loads the poster images into image views using Picasso library
     */
    public class FetchMovieDataTask extends AsyncTask<Void, Void, List<MovieEntry>> {
        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();
        private MovieGridAdapter movieAdapter;

        public FetchMovieDataTask(MovieGridAdapter movieAdapter) {
            this.movieAdapter = movieAdapter;
        }

        private String getAPIKey() {
            return getString(R.string.tmdb_api_key);
        }

        @Override
        protected void onPreExecute() {
            movieAdapter.clear();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MovieEntry> movieEntries) {
            movieAdapter.addAll(movieEntries);
            movieAdapter.notifyDataSetChanged();
            super.onPostExecute(movieEntries);
        }

        @Override
        protected List<MovieEntry> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;
            // Will contain parsed
            List<MovieEntry> moviesDetails;

            try {
                // Construct the URL for the OpenWeatherMap query
                final String URL_BASE = "https://api.themoviedb.org/3/discover/movie?";
                final String PARAM_API = "api_key";
                final String PARAM_SORT = "sort_by";
                final String PARAM_VOTE_COUNT = "vote_count.gte";
                final int VOTE_COUNT_THRESHOLD = 30;

                String sortOrder = getSortOrderFromPreferences();
                setCurrentSortOrder(sortOrder);

                Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                        .appendQueryParameter(PARAM_API, getAPIKey())
                        .appendQueryParameter(PARAM_SORT, sortOrder)
                        .appendQueryParameter(PARAM_VOTE_COUNT, Integer.toString(VOTE_COUNT_THRESHOLD));

                URL url = new URL(builder.build().toString());

                Log.v(LOG_TAG, url.toString());
                // Create the request to tmdb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

                moviesDetails = getMovieDetailsFromJSON(moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON couldn't be parsed: ", e);
                // If the code didn't successfully parse the JSON data, there is nothing to return
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return moviesDetails;
        }


        private List<MovieEntry> getMovieDetailsFromJSON(String moviesJsonStr)
                throws JSONException {
            List<MovieEntry> movieDetailsList = new ArrayList<>();

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_MOVIES_ARRAY = "results";

            JSONObject jsonObjFromTMDB = new JSONObject(moviesJsonStr);
            JSONArray jsonArrayOfMovies = jsonObjFromTMDB.getJSONArray(TMDB_MOVIES_ARRAY);

            for (int i = 0; i < jsonArrayOfMovies.length(); i++) {
                JSONObject movie = jsonArrayOfMovies.getJSONObject(i);
                try {
                    movieDetailsList.add(new MovieEntry(movie));
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "error parsing JSON ");
                }

            }
            return movieDetailsList;

        }
    }


    private class MovieGridAdapter extends BaseAdapter {


        private List<MovieEntry> mMovieEntryList = new ArrayList<>();

        public void clear() {
            if (mMovieEntryList != null) {
                mMovieEntryList.clear();
            }
        }

        @Override
        public int getCount() {
            return mMovieEntryList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMovieEntryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mMovieEntryList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(1, 1, 1, 1);
                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(getActivity()).load(mMovieEntryList.get(position).getPosterPath()).into(imageView);
            return imageView;
        }

        public void addAll(List<MovieEntry> movieEntries) {
            mMovieEntryList.addAll(movieEntries);
        }
    }
}
