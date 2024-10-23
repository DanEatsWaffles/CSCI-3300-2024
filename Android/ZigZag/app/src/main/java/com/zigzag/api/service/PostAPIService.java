package com.zigzag.api.service;

import com.zigzag.api.model.NewPost;
import com.zigzag.api.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostAPIService {
    @GET("posts")
    Call<List<Post>> getPosts(@Query("longitude") double longitude, @Query("latitude") double latitude);

    @GET("posts")
    Call<List<Post>> getPostsWithinDistance(@Query("longitude") double longitude, @Query("latitude") double latitude, @Query("distance") int distance);

    @GET("posts")
    Call<List<Post>> getPostsWithHashtag(@Query("longitude") double longitude, @Query("latitude") double latitude, @Query("hashtag") String hashtag);

    @POST("posts")
    Call<NewPost> createPost(@Body NewPost newPost, @Query("longitude") double longitude, @Query("latitude") double latitude);
}