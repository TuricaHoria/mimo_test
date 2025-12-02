package com.example.mimotest.core.network

import com.example.mimotest.feature.lesson.data.LessonApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    
    private const val BASE_URL = "https://mimochallenge.azurewebsites.net/"
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val lessonApiService: LessonApiService by lazy {
        retrofit.create(LessonApiService::class.java)
    }
}

