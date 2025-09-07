package com.example.termproject.api

import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApiService {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") apiKey: String,
        @Query("query") title: String,             //영화 제목
        @Query("year") year: String,               //개봉 년도
        @Query("language") language: String = "ko" //한글 설정
    ): TMDBResponse
}
