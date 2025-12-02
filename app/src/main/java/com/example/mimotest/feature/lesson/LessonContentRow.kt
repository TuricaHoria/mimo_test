package com.example.mimotest.feature.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.example.mimotest.R
import androidx.core.graphics.toColorInt

@Composable
fun LessonContentRow(
    lesson: Lesson,
    currentInputText: String,
    onInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val inputRange = lesson.input

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.lesson_row_bottom_padding))
            .heightIn(min = dimensionResource(R.dimen.lesson_row_min_height))
            .background(
                color = colorResource(R.color.lesson_row_background),
                shape = RoundedCornerShape(
                    dimensionResource(R.dimen.lesson_row_corner_radius)
                )
            )
            .padding(
                horizontal = dimensionResource(R.dimen.lesson_row_horizontal_padding),
                vertical = dimensionResource(R.dimen.lesson_row_vertical_padding)
            )
    ) {
        if (inputRange == null) {
            lesson.content.forEach { segment ->
                LessonTextSegment(segment = segment)
            }
        } else {
            LessonContentWithInput(
                lesson = lesson,
                inputRange = inputRange,
                currentInputText = currentInputText,
                onInputChanged = onInputChanged
            )
        }
    }
}

@Composable
private fun LessonContentWithInput(
    lesson: Lesson,
    inputRange: LessonInputRange,
    currentInputText: String,
    onInputChanged: (String) -> Unit
) {
    val fullText = lesson.content.joinToString(separator = "") { it.text }
    val safeStart = inputRange.startIndex.coerceIn(0, fullText.length)
    val safeEnd = inputRange.endIndex.coerceIn(safeStart, fullText.length)

    val inputSegmentInfo = findInputSegment(lesson.content, safeStart, safeEnd)

    if (inputSegmentInfo == null) {
        lesson.content.forEach { segment ->
            LessonTextSegment(segment = segment)
        }
    } else {
        lesson.content.forEachIndexed { index, segment ->
            when {
                index < inputSegmentInfo.segmentIndex -> {
                    LessonTextSegment(segment = segment)
                }
                index > inputSegmentInfo.segmentIndex -> {
                    LessonTextSegment(segment = segment)
                }
                else -> {
                    LessonSegmentWithInput(
                        segment = segment,
                        segmentStartIndex = inputSegmentInfo.segmentStartIndex,
                        inputStartIndex = safeStart,
                        inputEndIndex = safeEnd,
                        currentInputText = currentInputText,
                        onInputChanged = onInputChanged
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonSegmentWithInput(
    segment: LessonContentSegment,
    segmentStartIndex: Int,
    inputStartIndex: Int,
    inputEndIndex: Int,
    currentInputText: String,
    onInputChanged: (String) -> Unit
) {
    val localStart = inputStartIndex - segmentStartIndex
    val localEnd = inputEndIndex - segmentStartIndex
    val beforePart = segment.text.take(localStart.coerceAtLeast(0))
    val afterPart = segment.text.substring(localEnd.coerceAtMost(segment.text.length))

    if (beforePart.isNotEmpty()) {
        LessonTextSegment(
            text = beforePart,
            color = segment.color
        )
    }

    OutlinedTextField(
        value = currentInputText,
        onValueChange = onInputChanged,
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
        LessonTextSegment(
            text = afterPart,
            color = segment.color
        )
    }
}

@Composable
fun LessonTextSegment(
    segment: LessonContentSegment,
    modifier: Modifier = Modifier
) {
    LessonTextSegment(
        text = segment.text,
        color = segment.color,
        modifier = modifier
    )
}

@Composable
private fun LessonTextSegment(
    text: String,
    color: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color.toColorOrDefault(),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

private data class InputSegmentInfo(
    val segmentIndex: Int,
    val segmentStartIndex: Int
)

private fun findInputSegment(
    segments: List<LessonContentSegment>,
    inputStart: Int,
    inputEnd: Int
): InputSegmentInfo? {
    var cumulativeIndex = 0
    var inputSegmentIndex = -1
    var inputSegmentStart = 0

    segments.forEachIndexed { index, segment ->
        val segStart = cumulativeIndex
        val segEnd = cumulativeIndex + segment.text.length
        
        if (inputStart >= segStart && inputEnd <= segEnd && inputSegmentIndex == -1) {
            inputSegmentIndex = index
            inputSegmentStart = segStart
        }
        
        cumulativeIndex = segEnd
    }

    return if (inputSegmentIndex != -1) {
        InputSegmentInfo(
            segmentIndex = inputSegmentIndex,
            segmentStartIndex = inputSegmentStart
        )
    } else {
        null
    }
}

private fun String.toColorOrDefault(): Color =
    try {
        Color(this.toColorInt())
    } catch (_: IllegalArgumentException) {
        Color.Unspecified
    }

