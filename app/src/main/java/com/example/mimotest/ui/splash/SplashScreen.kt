package com.example.mimotest.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mimotest.R

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.mimo_banner_2),
        contentDescription = stringResource(R.string.splash_banner_content_description),
        contentScale = ContentScale.FillHeight,
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}

