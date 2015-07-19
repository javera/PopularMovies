package com.mjaworski1988.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        // Get the movie entry from the intent
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MovieEntry.EXTRA_MOVIE_ENTRY)) {
            MovieEntry mMovieEntry = intent.getParcelableExtra(MovieEntry.EXTRA_MOVIE_ENTRY);

            TextView title = (TextView) rootView.findViewById(R.id.details_title);
            title.setText(mMovieEntry.getTitle());
            TextView year = (TextView) rootView.findViewById(R.id.details_year);
            year.setText(mMovieEntry.getReleaseYear());
            TextView rating = (TextView) rootView.findViewById(R.id.details_rating);
            rating.setText(mMovieEntry.getVoteAverage() + "/10");
            TextView overview = (TextView) rootView.findViewById(R.id.details_overview);
            overview.setText(mMovieEntry.getOverview());

            ImageView poster = (ImageView) rootView.findViewById(R.id.details_img_poster);
            Picasso.with(getActivity()).load(mMovieEntry.getPosterPath()).into(poster);

        }

        return rootView;
    }

}
