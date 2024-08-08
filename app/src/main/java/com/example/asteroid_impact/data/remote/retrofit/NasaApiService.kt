package com.example.asteroid_impact.data.remote.retrofit

import com.example.asteroid_impact.Constants
import com.example.asteroid_impact.data.model.NeoFeedResponse
import com.example.asteroid_impact.data.model.NeoLookupResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("neo/rest/v1/feed") // ?가 필요할 수도 있음
    suspend fun getNeoFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apikey: String = Constants.NASA_API_KEY
    ): NeoFeedResponse

    @GET("neo/rest/v1/neo/")
    suspend fun getNeoLookup(
        @Query("asteroid_id") asteroidId: Int,
        @Query("api_key") apikey: String = Constants.NASA_API_KEY
    ): NeoLookupResponse
}