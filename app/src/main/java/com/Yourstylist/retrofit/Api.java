package com.Yourstylist.retrofit;


import com.Yourstylist.retrofit.LoginRequest;
import com.Yourstylist.retrofit.LoginResponse;
import com.Yourstylist.retrofit.SignUpRequest;
import com.Yourstylist.retrofit.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {
    @Headers("Content-type: application/json")
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers("Content-type: application/json")
    @POST("/signup")
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);

    @Headers("Content-type: application/json")
    @GET("/feed")
    Call<ArticleResponse> getArticles();

    @Headers("Content-type: application/json")
    @POST("/feedLike")
    Call<LikeArticleResponse> likeArticle(@Body LikeArticleRequest likeArticleRequest);

    @Headers("Content-type: application/json")
    @POST("/feedComment")
    Call<CommentArticleResponse> commentArticle(@Body CommentArticleRequest commentArticleRequest);

    @Headers("Content-type: application/json")
    @GET("/getAllPhotos")
    Call<PhotolistResponse> getAllPhotos();
}
