package com.rcdev.popularmovies.utils;

import com.rcdev.popularmovies.objects.MovieItem;
import com.rcdev.popularmovies.objects.ReviewItem;
import com.rcdev.popularmovies.objects.TrailerItem;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by coreyestes on 4/7/16.
 */
public interface MovieAPI {


    @GET("/3/discover/movie")
    void fetchMovies(
            @Query("sort_by") String mSort,
            @Query("api_key") String mApiKey,
            @Query("language") String lang,
            @Query("page") Integer page,
            Callback<MovieItem> cb
    );




    @GET("/3/movie/{id}/reviews")
    void fetchReview(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<ReviewItem> cb
    );

    @GET("/3/movie/{id}/videos")
    void fetchVideos(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<TrailerItem> cb
    );

    @GET("/3/search/movie")
    void searchMovie(
            @Query("api_key") String mApiKey,
            @Query("query") String mquery,

            Callback<MovieItem> callback);

}
