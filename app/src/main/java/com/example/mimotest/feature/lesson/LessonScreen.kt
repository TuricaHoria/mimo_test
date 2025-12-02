package com.example.mimotest.feature.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.heightIn
import com.example.mimotest.feature.lesson.LessonContract.Intent
import com.example.mimotest.feature.lesson.LessonContract.State
import com.example.mimotest.R
import androidx.core.graphics.toColorInt

@Composable
fun LessonRoute(
    viewModel: LessonViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(Intent.LoadLessons)
    }

    LessonScreen(
        state = state,
        onIntent = viewModel::processIntent,
        modifier = modifier
    )
}

@Composable
fun LessonScreen(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isDone -> DoneScreen(modifier)
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
    val inputRange = currentLesson?.input

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
            
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.lesson_row_bottom_padding))
                .heightIn(min = dimensionResource(R.dimen.lesson_row_min_height))
                .background(
                    color = colorResource(R.color.lesson_row_background),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        dimensionResource(R.dimen.lesson_row_corner_radius)
                    )
                )
                .padding(
                    horizontal = dimensionResource(R.dimen.lesson_row_horizontal_padding),
                    vertical = dimensionResource(R.dimen.lesson_row_vertical_padding)
                )
        ) {
            if (currentLesson == null) {
                // No leson data yet keep row empty to avoid extra preview text
            } else if (inputRange == null) {
                currentLesson.content.forEach { segment ->
                    Text(
                        text = segment.text,
                        color = segment.color.toColorOrDefault(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                val fullText = currentLesson.content.joinToString(separator = "") { it.text }
                val safeStart = inputRange.startIndex.coerceIn(0, fullText.length)
                val safeEnd = inputRange.endIndex.coerceIn(safeStart, fullText.length)

                var cumulativeIndex = 0
                var inputSegmentIndex = -1
                var inputSegmentStart = 0

                currentLesson.content.forEachIndexed { index, segment ->
                    val segStart = cumulativeIndex
                    val segEnd = cumulativeIndex + segment.text.length
                    if (safeStart >= segStart && safeEnd <= segEnd && inputSegmentIndex == -1) {
                        inputSegmentIndex = index
                        inputSegmentStart = segStart
                    }
                    cumulativeIndex = segEnd
                }

                if (inputSegmentIndex == -1) {
                    currentLesson.content.forEach { segment ->
                        Text(
                            text = segment.text,
                            color = segment.color.toColorOrDefault(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    currentLesson.content.forEachIndexed { index, segment ->
                        when {
                            index < inputSegmentIndex -> {
                                Text(
                                    text = segment.text,
                                    color = segment.color.toColorOrDefault(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            index > inputSegmentIndex -> {
                                Text(
                                    text = segment.text,
                                    color = segment.color.toColorOrDefault(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            else -> {
                                val segStart = inputSegmentStart
                                val localStart = safeStart - segStart
                                val localEnd = safeEnd - segStart
                                val beforePart = segment.text.substring(0, localStart.coerceAtLeast(0))
                                val afterPart = segment.text.substring(localEnd.coerceAtMost(segment.text.length))

                                if (beforePart.isNotEmpty()) {
                                    Text(
                                        text = beforePart,
                                        color = segment.color.toColorOrDefault(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                OutlinedTextField(
                                    value = state.currentInputText,
                                    onValueChange = { text -> onIntent(Intent.InputChanged(text)) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = colorResource(R.color.lesson_input_text_color)
                                    ),
                                    modifier = Modifier.height(dimensionResource(R.dimen.lesson_input_height)),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = colorResource(R.color.lesson_input_text_color),
                                        unfocusedTextColor = colorResource(R.color.lesson_input_text_color),
                                        cursorColor = MaterialTheme.colorScheme.primary,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                                        focusedContainerColor = colorResource(R.color.lesson_input_container_color),
                                        unfocusedContainerColor = colorResource(R.color.lesson_input_container_color)
                                    )
                                )

                                if (afterPart.isNotEmpty()) {
                                    Text(
                                        text = afterPart,
                                        color = segment.color.toColorOrDefault(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.lesson_error_horizontal_padding))
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
    }
}

private fun String.toColorOrDefault(): Color =
    try {
        Color(this.toColorInt())
    } catch (_: IllegalArgumentException) {
        Color.Unspecified
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
        modifier = Modifier
    )
}