package com.mjaworski1988.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marek on 17/07/2015.
 */
public class MovieEntry {

    public MovieEntry(JSONObject movie) throws JSONException {
        final String TMDB_MOVIES_ID = "id";
        final String TMDB_MOVIES_LANGUAGE = "original_language";
        final String TMDB_MOVIES_OVERVIEW = "overview";
        final String TMDB_MOVIES_TITLE = "title";
        final String TMDB_MOVIES_RELEASE_DATE = "release_date";
        final String TMDB_MOVIES_POSTER_PATH = "poster_path";
        final String TMDB_MOVIES_VOTE_AVG = "vote_average";

        id = movie.getInt(TMDB_MOVIES_ID);
        language = movie.getString(TMDB_MOVIES_LANGUAGE);
        overview = movie.getString(TMDB_MOVIES_OVERVIEW);
        title = movie.getString(TMDB_MOVIES_TITLE);
        releaseDate = movie.getString(TMDB_MOVIES_RELEASE_DATE);
        posterPath = movie.getString(TMDB_MOVIES_POSTER_PATH);
        voteAverage = movie.getDouble(TMDB_MOVIES_VOTE_AVG);
    }

    private int id;
    private String language;
    private String overview;
    private String title;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;

    public int getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }
}
