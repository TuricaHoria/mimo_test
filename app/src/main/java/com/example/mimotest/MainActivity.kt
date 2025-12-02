package com.example.mimotest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mimotest.feature.lesson.LessonRoute
import com.example.mimotest.feature.lesson.LessonViewModel
import com.example.mimotest.feature.lesson.data.LessonRemoteDataSourceImpl
import com.example.mimotest.feature.lesson.data.LessonRepository
import com.example.mimotest.feature.lesson.data.LessonRepositoryImpl
import com.example.mimotest.feature.lesson.data.RoomLessonCompletionStore
import com.example.mimotest.core.network.NetworkModule
import com.example.mimotest.ui.splash.SplashScreen
import com.example.mimotest.ui.theme.MimoTestTheme
import com.example.mimotest.R
import androidx.compose.ui.res.integerResource
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val lessonViewModel: LessonViewModel by viewModels {
        LessonViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MimoTestTheme {
                AppContent(lessonViewModel = lessonViewModel)
            }
        }
    }
}

@Composable
private fun AppContent(lessonViewModel: LessonViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    val splashDuration = integerResource(R.integer.splash_screen_duration_millis)

    LaunchedEffect(Unit) {
        delay(splashDuration.toLong())
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            LessonRoute(
                viewModel = lessonViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


class LessonViewModelFactory(
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            val apiService = NetworkModule.lessonApiService
            val remoteDataSource = LessonRemoteDataSourceImpl(apiService)
            val completionStore = RoomLessonCompletionStore(context)
            val repository: LessonRepository = LessonRepositoryImpl(
                remoteDataSource = remoteDataSource,
                completionStore = completionStore
            )
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}
