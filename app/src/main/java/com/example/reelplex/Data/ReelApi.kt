package com.example.reelplex.Data

import com.example.reelplex.Models.ReelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReelApi {

    @GET("download")
    suspend fun getReel(
        @Query("url") url: String,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String
    ): Response<ReelResponse>
}