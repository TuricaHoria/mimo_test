package com.example.mimotest.feature.lesson

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.mimotest.R
import kotlinx.coroutines.delay

@Composable
fun CorrectAnimationOverlay(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit
) {
    var started by remember { mutableStateOf(false) }
    val delayMillis = integerResource(R.integer.correct_animation_delay_millis).toLong()
    
    LaunchedEffect(Unit) {
        started = true
        delay(delayMillis)
        onAnimationFinished()
    }

    val durationMillis = integerResource(R.integer.correct_animation_duration_millis)

    val scale by animateFloatAsState(
        targetValue = if (started) integerResource(R.integer.correct_animation_target_scale) / 100f else integerResource(R.integer.correct_animation_initial_scale) / 100f,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "checkScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (started) integerResource(R.integer.correct_animation_target_alpha) / 100f else integerResource(R.integer.correct_animation_initial_alpha) / 100f,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "checkAlpha"
    )

    val overlayColor = colorResource(R.color.overlay_background)
    val checkmarkColor = colorResource(R.color.checkmark_green)
    val textColor = colorResource(R.color.white)
    val spacerHeight = dimensionResource(R.dimen.correct_animation_spacer_height)

    Box(
        modifier = modifier
            .background(overlayColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.checkmark_symbol),
                color = checkmarkColor,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            Text(
                text = stringResource(R.string.correct_message),
                color = textColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.graphicsLayer(alpha = alpha)
            )
        }
    }
}

@Composable
fun WrongAnimationOverlay(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit
) {
    var started by remember { mutableStateOf(false) }
    val delayMillis = integerResource(R.integer.wrong_animation_delay_millis).toLong()
    
    LaunchedEffect(Unit) {
        started = true
        delay(delayMillis)
        onAnimationFinished()
    }

    val durationMillis = integerResource(R.integer.wrong_animation_duration_millis)

    val scale by animateFloatAsState(
        targetValue = if (started) integerResource(R.integer.wrong_animation_target_scale) / 100f else integerResource(R.integer.wrong_animation_initial_scale) / 100f,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "wrongScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (started) integerResource(R.integer.wrong_animation_target_alpha) / 100f else integerResource(R.integer.wrong_animation_initial_alpha) / 100f,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "wrongAlpha"
    )

    val overlayColor = colorResource(R.color.overlay_background)
    val crossColor = colorResource(R.color.cross_red)
    val textColor = colorResource(R.color.white)
    val spacerHeight = dimensionResource(R.dimen.wrong_animation_spacer_height)

    Box(
        modifier = modifier
            .background(overlayColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.cross_symbol),
                color = crossColor,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            Text(
                text = stringResource(R.string.wrong_message),
                color = textColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.graphicsLayer(alpha = alpha)
            )
        }
    }
}

@Composable
fun DoneScreen(
    onStartAgain: () -> Unit,
    onCloseApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        started = true
    }

    val logoEntranceScale by animateFloatAsState(
        targetValue = if (started) integerResource(R.integer.done_screen_logo_target_scale) / 100f else integerResource(R.integer.done_screen_logo_initial_scale) / 100f,
        animationSpec = tween(
            durationMillis = integerResource(R.integer.done_screen_logo_entrance_duration_millis),
            easing = FastOutSlowInEasing
        ),
        label = "logoEntranceScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = integerResource(R.integer.logo_pulse_initial_scale) / 100f,
        targetValue = integerResource(R.integer.logo_pulse_target_scale) / 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = integerResource(R.integer.logo_pulse_duration_millis),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val textPulseScale by infiniteTransition.animateFloat(
        initialValue = integerResource(R.integer.text_pulse_initial_scale) / 100f,
        targetValue = integerResource(R.integer.text_pulse_target_scale) / 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = integerResource(R.integer.text_pulse_duration_millis),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textPulseScale"
    )

    val doneBackgroundColor = colorResource(R.color.done_screen_background)
    val doneTextColor = colorResource(R.color.done_text_color)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(doneBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.done_screen_horizontal_padding))
        ) {
            Image(
                painter = painterResource(id = R.drawable.mimo_icon),
                contentDescription = stringResource(R.string.mimo_logo_content_description),
                modifier = Modifier
                    .height(dimensionResource(R.dimen.done_screen_logo_height))
                    .graphicsLayer(
                        scaleX = logoEntranceScale * pulseScale,
                        scaleY = logoEntranceScale * pulseScale
                    ),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.done_screen_spacer_height)))
            Text(
                stringResource(R.string.done_text),
                style = MaterialTheme.typography.headlineLarge,
                color = doneTextColor,
                modifier = Modifier.graphicsLayer(
                    scaleX = textPulseScale,
                    scaleY = textPulseScale
                )
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.done_screen_buttons_spacer_height)))
            Button(
                onClick = onStartAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.done_screen_button_height)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.done_screen_button_corner_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = colorResource(R.color.done_button_content_color)
                )
            ) {
                Text(stringResource(R.string.start_again_button))
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.done_screen_button_spacing)))
            Button(
                onClick = onCloseApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.done_screen_button_height)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.done_screen_button_corner_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.done_close_button_background),
                    contentColor = colorResource(R.color.done_button_content_color)
                )
            ) {
                Text(stringResource(R.string.close_button))
            }
        }
    }
}

