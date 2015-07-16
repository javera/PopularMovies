package com.mjaworski1988.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
    private GridView mMovieGrid;

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieGrid = (GridView) rootView.findViewById(R.id.grid_movie_posters);
        MovieGridAdapter movieAdapter = new MovieGridAdapter();
        mMovieGrid.setAdapter(movieAdapter);

        new FetchMovieDataTask(movieAdapter).execute();

        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;

    }

    public class FetchMovieDataTask extends AsyncTask<Void, Void, List<MovieEntry>> {
        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

        MovieGridAdapter movieAdapter;

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
                final String URL_BASE = "https://api.themoviedb.org/3/movie/popular/";
                final String PARAM_API = "api_key";

                Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                        .appendQueryParameter(PARAM_API, getAPIKey());

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


        private List<MovieEntry> mMovieEntryList;

        public void clear() {
            if (mMovieEntryList != null) {
                mMovieEntryList.clear();
            }
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = new TextView(getActivity());
            view.setText(mMovieEntryList.get(position).getTitle());
            return view;
//            ImageView imageView;
//            if (convertView == null) {
//                // if it's not recycled, initialize some attributes
//                imageView = new ImageView(getActivity());
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imageView.setPadding(8, 8, 8, 8);
//                imageView.setBackgroundColor(getActivity().getResources().getColor(R.color.material_deep_teal_500));
//                imageView.setMaxHeight(100);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//
////            imageView.setImageResource(mThumbIds[position]);
//            return imageView;
        }

        public void addAll(List<MovieEntry> movieEntries) {
            mMovieEntryList.addAll(movieEntries);
        }
    }
}
