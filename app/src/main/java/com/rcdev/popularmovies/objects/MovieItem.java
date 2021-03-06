package com.rcdev.popularmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.rcdev.popularmovies.utils.Constants;

/**
 * Created by coreyestes on 7/22/15.
 */
public class MovieItem implements Parcelable {
    private String id;
    private String title;
    private String poster;
    private String overview;
    private String release_date;
    private String path;
    private String popularity;
    private String vote_average;
    private String vote_count;
    private String full_poster;
    private int image;

    public MovieItem() {
        super();
    }

    protected MovieItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        path = in.readString();
        full_poster = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public MovieItem(String id, String title,  String full_poster,  String overview, String release_date, String vote_average) {
        this.id = id;
        this.title = title;
        this.full_poster = full_poster;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
    }


    public String getFull_poster() {
        return full_poster;
    }

    public void setFull_poster(String full_poster) {
        this.full_poster = Constants.BASE_IMAGE_PATH + path;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getId() {
        return id;
    }

    public int getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeString(full_poster);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(vote_average);
    }
}



