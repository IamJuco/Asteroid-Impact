package com.example.asteroid_impact.data.remote.retrofit

import com.example.asteroid_impact.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.NASA_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

// TODO ApiService 인터페이스 만들것 ( NeoFeedService, NeoLookupService )

    val getNeoFeedService: NeoFeedService by lazy {
        retrofit.create(NeoFeedService::class.java)
    }

    val getNeoLookupService: NeoLookupService by lazy {
        retrofit.create(NeoLookupService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .build()
    }
}