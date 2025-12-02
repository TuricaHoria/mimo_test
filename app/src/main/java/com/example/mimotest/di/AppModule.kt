package com.example.mimotest.di

import android.content.Context
import com.example.mimotest.feature.lesson.data.LessonApiService
import com.example.mimotest.feature.lesson.data.LessonCompletionStore
import com.example.mimotest.feature.lesson.data.LessonDao
import com.example.mimotest.feature.lesson.data.LessonDatabase
import com.example.mimotest.feature.lesson.data.LessonRemoteDataSource
import com.example.mimotest.feature.lesson.data.LessonRemoteDataSourceImpl
import com.example.mimotest.feature.lesson.data.LessonRepository
import com.example.mimotest.feature.lesson.data.LessonRepositoryImpl
import com.example.mimotest.feature.lesson.data.RoomLessonCompletionStore
import com.example.mimotest.feature.lesson.LessonViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://mimochallenge.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    single<LessonApiService> {
        get<Retrofit>().create(LessonApiService::class.java)
    }
    
    single<LessonDatabase> {
        LessonDatabase.getDatabase(androidContext())
    }
    
    single<LessonDao> {
        get<LessonDatabase>().lessonDao()
    }
    
    single<LessonRemoteDataSource> {
        LessonRemoteDataSourceImpl(get())
    }
    
    single<LessonCompletionStore> {
        RoomLessonCompletionStore(androidContext())
    }
    
    single<LessonRepository> {
        LessonRepositoryImpl(
            remoteDataSource = get(),
            completionStore = get()
        )
    }
    
    viewModel { LessonViewModel(get()) }
}

