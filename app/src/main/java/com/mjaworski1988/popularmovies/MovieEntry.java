package com.mjaworski1988.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marek on 17/07/2015.
 */
public class MovieEntry implements Parcelable {

    public static final String EXTRA_MOVIE_ENTRY = MovieEntry.class.getPackage().getName() + MovieEntry.class.getSimpleName();

    protected MovieEntry(Parcel in) {
        id = in.readInt();
        language = in.readString();
        overview = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(language);
        out.writeString(overview);
        out.writeString(title);
        out.writeString(releaseDate);
        out.writeString(posterPath);
        out.writeDouble(voteAverage);

    }

    public static final Parcelable.Creator<MovieEntry> CREATOR = new Parcelable.Creator<MovieEntry>() {
        @Override
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        @Override
        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };

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

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String RESOLUTION = "w342/";

        return BASE_URL.concat(RESOLUTION).concat(posterPath);
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseYear() {
        if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        } else {
            return "";
        }

    }
}
