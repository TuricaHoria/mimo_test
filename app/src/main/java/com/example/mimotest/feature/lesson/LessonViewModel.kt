package com.example.mimotest.feature.lesson

import androidx.lifecycle.viewModelScope
import com.example.mimotest.core.mvi.MviViewModel
import com.example.mimotest.feature.lesson.LessonContract.Effect
import com.example.mimotest.feature.lesson.LessonContract.Intent
import com.example.mimotest.feature.lesson.LessonContract.State
import com.example.mimotest.feature.lesson.data.LessonRepository
import kotlinx.coroutines.launch

class LessonViewModel(
    private val repository: LessonRepository
) : MviViewModel<State, Intent, Effect>(State()) {

    override fun processIntent(intent: Intent) {
        when (intent) {
            Intent.LoadLessons -> loadLessons()
            Intent.Retry -> loadLessons()
            is Intent.InputChanged -> onInputChanged(intent.text)
            Intent.CheckAnswerClicked -> checkAnswer()
            Intent.NextLessonClicked -> goToNextLesson()
            Intent.CorrectAnimationFinished -> onCorrectAnimationFinished()
            Intent.WrongAnimationFinished -> onWrongAnimationFinished()
        }
    }

    private fun loadLessons() {
        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.getLessons()

            result
                .onSuccess { lessons ->
                    lessons.firstOrNull()?.let { firstLesson ->
                        viewModelScope.launch {
                            repository.markLessonStarted(firstLesson.id.toString())
                        }
                    }
                    
                    setState {
                        copy(
                            isLoading = false,
                            lessons = lessons,
                            currentIndex = 0,
                            currentInputText = "",
                            isCurrentLessonSolved = false,
                            isNextButtonEnabled = shouldEnableNextButton(
                                lesson = lessons.firstOrNull(),
                                currentInput = ""
                            ),
                            showCorrectAnimation = false,
                            showWrongAnimation = false,
                            isDone = lessons.isEmpty()
                        )
                    }
                }
                .onFailure { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message
                        )
                    }
                }
        }
    }

    private fun onInputChanged(text: String) {
        val currentLesson = state.value.lessons.getOrNull(state.value.currentIndex)
        setState {
            copy(
                currentInputText = text,
                errorMessage = null,
                showCorrectAnimation = false,
                showWrongAnimation = false,
                isNextButtonEnabled = shouldEnableNextButton(
                    lesson = currentLesson,
                    currentInput = text
                )
            )
        }
    }


    private fun checkAnswer() {
        val currentState = state.value
        val currentLesson = currentState.lessons.getOrNull(currentState.currentIndex)

        if (currentLesson == null) return

        val inputRange = currentLesson.input

        if (inputRange == null) {
            markCompletedAndAdvance(currentLessonId = currentLesson.id.toString())
            return
        }

        val fullText = currentLesson.content.joinToString(separator = "") { it.text }
        val safeStart = inputRange.startIndex.coerceIn(0, fullText.length)
        val safeEnd = inputRange.endIndex.coerceIn(safeStart, fullText.length)
        val expected = fullText.substring(safeStart, safeEnd)
        val given = currentState.currentInputText

        if (given == expected) {
            markCompletedAndAdvance(currentLessonId = currentLesson.id.toString())
        } else {
            setState {
                copy(
                    isCurrentLessonSolved = false,
                    showWrongAnimation = true,
                    errorMessage = null
                )
            }
        }
    }

    private fun goToNextLesson() {
        val currentState = state.value
        if (!currentState.isCurrentLessonSolved) {
            viewModelScope.launch {
                emitEvent(Effect.ShowMessage("Please solve the lesson first"))
            }
            return
        }

        val nextIndex = currentState.currentIndex + 1
        val lastIndex = currentState.lessons.lastIndex

        if (nextIndex > lastIndex) {
            setState { copy(isDone = true) }
            viewModelScope.launch {
                emitEvent(Effect.NavigateToDone)
            }
        } else {
            val nextLesson = currentState.lessons.getOrNull(nextIndex)
            nextLesson?.let { lesson ->
                viewModelScope.launch {
                    repository.markLessonStarted(lesson.id.toString())
                }
            }
            
            setState {
                copy(
                    currentIndex = nextIndex,
                    currentInputText = "",
                    isCurrentLessonSolved = false,
                    isNextButtonEnabled = shouldEnableNextButton(
                        lesson = nextLesson,
                        currentInput = ""
                    )
                )
            }
        }
    }

    private fun markCompletedAndAdvance(currentLessonId: String) {
        val currentState = state.value
        val isLastLesson = currentState.currentIndex == currentState.lessons.lastIndex

        viewModelScope.launch {
            repository.markLessonCompleted(currentLessonId)
        }

        if (isLastLesson) {
            setState { copy(isCurrentLessonSolved = true, errorMessage = null, showCorrectAnimation = false) }
            goToNextLesson()
        } else {
            setState { copy(isCurrentLessonSolved = true, errorMessage = null, showCorrectAnimation = true) }
        }
    }

    private fun onCorrectAnimationFinished() {
        setState { copy(showCorrectAnimation = false) }
        goToNextLesson()
    }

    private fun onWrongAnimationFinished() {
        setState { copy(showWrongAnimation = false) }
    }


    private fun shouldEnableNextButton(
        lesson: Lesson?,
        currentInput: String
    ): Boolean {
        val hasInputInteraction = lesson?.input != null
        return if (!hasInputInteraction) {
            true
        } else {
            currentInput.isNotEmpty()
        }
    }
}


