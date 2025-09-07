package com.example.termproject.api

data class TMDBResponse(val results: List<TMDBMovie>)

data class TMDBMovie(
    val title: String,           //영화 제목
    val release_date: String,    //개봉일
    val poster_path: String?,    //포스터 이미지 경로
    val overview: String,        //줄거리
    val id: Int                  //TMDB 고유 영화 ID
)

