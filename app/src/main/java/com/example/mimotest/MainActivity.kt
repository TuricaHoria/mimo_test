package com.example.mimotest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.mimotest.ui.splash.SplashScreen
import com.example.mimotest.ui.theme.MimoTestTheme
import com.example.mimotest.R
import androidx.compose.ui.res.integerResource
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val lessonViewModel: LessonViewModel by viewModel()

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
