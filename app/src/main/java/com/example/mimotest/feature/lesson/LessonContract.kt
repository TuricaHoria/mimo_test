package com.example.mimotest.feature.lesson

import com.example.mimotest.core.mvi.MviIntent
import com.example.mimotest.core.mvi.MviSingleEvent
import com.example.mimotest.core.mvi.MviViewState

data class Lesson(
    val id: Int,
    val content: List<LessonContentSegment>,
    val input: LessonInputRange?
)

data class LessonContentSegment(
    val color: String,
    val text: String
)

data class LessonInputRange(
    val startIndex: Int,
    val endIndex: Int
)

object LessonContract {

    data class State(
        val isLoading: Boolean = false,
        val lessons: List<Lesson> = emptyList(),
        val currentIndex: Int = 0,
        val currentInputText: String = "",
        val isCurrentLessonSolved: Boolean = false,
        val isNextButtonEnabled: Boolean = true,
        val showCorrectAnimation: Boolean = false,
        val showWrongAnimation: Boolean = false,
        val errorMessage: String? = null,
        val isDone: Boolean = false
    ) : MviViewState

    sealed interface Intent : MviIntent {
        data object LoadLessons : Intent
        data object Retry : Intent
        data class InputChanged(val text: String) : Intent
        data object CheckAnswerClicked : Intent
        data object NextLessonClicked : Intent
        data object CorrectAnimationFinished : Intent
        data object WrongAnimationFinished : Intent
    }

    sealed interface Effect : MviSingleEvent {
        data object NavigateToDone : Effect
        data class ShowMessage(val message: String) : Effect
    }
}


