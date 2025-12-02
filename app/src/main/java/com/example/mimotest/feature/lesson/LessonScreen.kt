package com.example.mimotest.feature.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.mimotest.feature.lesson.LessonContract.Intent
import com.example.mimotest.feature.lesson.LessonContract.State
import com.example.mimotest.R

@Composable
fun LessonRoute(
    viewModel: LessonViewModel,
    onCloseApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(Intent.LoadLessons)
    }

    LessonScreen(
        state = state,
        onIntent = viewModel::processIntent,
        onCloseApp = onCloseApp,
        modifier = modifier
    )
}

@Composable
fun LessonScreen(
    state: State,
    onIntent: (Intent) -> Unit,
    onCloseApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isDone -> DoneScreen(
            onStartAgain = { onIntent(Intent.StartAgain) },
            onCloseApp = onCloseApp,
            modifier = modifier
        )
        state.isLoading -> LoadingScreen(modifier)
        else -> LessonContent(
            state = state,
            onIntent = onIntent,
            modifier = modifier
        )
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
}

@Composable
private fun LessonContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLesson = state.lessons.getOrNull(state.currentIndex)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lesson_screen_background))
                .padding(
                    top = dimensionResource(R.dimen.lesson_screen_top_padding),
                    bottom = dimensionResource(R.dimen.lesson_screen_bottom_padding)
                )
        ) {
            Text(
                text = stringResource(R.string.lesson_title, state.currentIndex + 1),
                style = MaterialTheme.typography.titleLarge,
                color = colorResource(R.color.lesson_title_text_color),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.lesson_title_bottom_padding))
            )
            
            if (currentLesson != null) {
                LessonContentRow(
                    lesson = currentLesson,
                    currentInputText = state.currentInputText,
                    onInputChanged = { text -> onIntent(Intent.InputChanged(text)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }


        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.lesson_spacer_height)))

        Button(
            onClick = { onIntent(Intent.CheckAnswerClicked) },
            enabled = state.isNextButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.lesson_button_height))
                .padding(bottom = dimensionResource(R.dimen.lesson_button_bottom_padding)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.lesson_button_corner_radius)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = colorResource(R.color.lesson_button_content_color),
                disabledContainerColor = colorResource(R.color.lesson_button_disabled_container),
                disabledContentColor = colorResource(R.color.lesson_button_disabled_content)
            )
        ) {
            Text(stringResource(R.string.run_button))
        }
        }
        
        if (state.showCorrectAnimation) {
            CorrectAnimationOverlay(
                modifier = Modifier.fillMaxSize(),
                onAnimationFinished = { onIntent(Intent.CorrectAnimationFinished) }
            )
        }
        
        if (state.showWrongAnimation) {
            WrongAnimationOverlay(
                modifier = Modifier.fillMaxSize(),
                onAnimationFinished = { onIntent(Intent.WrongAnimationFinished) }
            )
        }
        
        state.errorMessage?.let { message ->
            ErrorDialog(
                message = message,
                onDismiss = { onIntent(Intent.DismissError) },
                onRetry = { onIntent(Intent.Retry) }
            )
        }
    }
}

@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.error_dialog_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry_button))
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Lesson - Normal")
@Composable
private fun LessonScreenPreview() {
    val lesson = Lesson(
        id = 5,
        content = listOf(
            LessonContentSegment(color = "#000000", text = "Hello "),
            LessonContentSegment(color = "#000000", text = "World"),
            LessonContentSegment(color = "#000000", text = "!")
        ),
        input = LessonInputRange(
            startIndex = 6,
            endIndex = 11
        )
    )

    val state = State(
        isLoading = false,
        lessons = listOf(lesson),
        currentIndex = 0,
        currentInputText = "",
        isCurrentLessonSolved = false,
        isNextButtonEnabled = false,
        errorMessage = null,
        isDone = false
    )

    LessonScreen(
        state = state,
        onIntent = {},
        onCloseApp = {},
        modifier = Modifier
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Lesson - Loading")
@Composable
private fun LessonLoadingPreview() {
    LoadingScreen(
        modifier = Modifier
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Lesson - Error")
@Composable
private fun LessonErrorPreview() {
    ErrorScreen(
        message = stringResource(R.string.error_message_preview),
        onRetry = {},
        modifier = Modifier
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Lesson - Done")
@Composable
private fun LessonDonePreview() {
    DoneScreen(
        onStartAgain = {},
        onCloseApp = {},
        modifier = Modifier
    )
}