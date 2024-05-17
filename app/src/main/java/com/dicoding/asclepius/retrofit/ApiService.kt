package com.dicoding.asclepius.retrofit

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.response.ResponseCancerNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(BuildConfig.Key)
    fun getNews(
        @Query("Cancer") query: String
    ): Call<ResponseCancerNews>
}