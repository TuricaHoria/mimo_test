package com.example.mimotest.feature.lesson

import app.cash.turbine.test
import com.example.mimotest.feature.lesson.LessonContract.Effect
import com.example.mimotest.feature.lesson.LessonContract.Intent
import com.example.mimotest.feature.lesson.LessonContract.State
import com.example.mimotest.feature.lesson.data.LessonRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    private lateinit var repository: LessonRepository
    private lateinit var viewModel: LessonViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = LessonViewModel(repository)
    }

    @Test
    fun `initial state is correct`() = runTest(testDispatcher) {
        val initialState = viewModel.state.value
        assertEquals(false, initialState.isLoading)
        assertEquals(emptyList<Lesson>(), initialState.lessons)
        assertEquals(0, initialState.currentIndex)
        assertEquals("", initialState.currentInputText)
        assertEquals(false, initialState.isCurrentLessonSolved)
        assertEquals(true, initialState.isNextButtonEnabled)
        assertEquals(false, initialState.showCorrectAnimation)
        assertEquals(false, initialState.showWrongAnimation)
        assertNull(initialState.errorMessage)
        assertEquals(false, initialState.isDone)
    }

    @Test
    fun `loadLessons success updates state with lessons`() = runTest(testDispatcher) {
        val lessons = listOf(
            createTestLesson(id = 1, hasInput = false),
            createTestLesson(id = 2, hasInput = true)
        )

        coEvery { repository.getLessons() } returns Result.success(lessons)
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(lessons, state.lessons)
        assertEquals(0, state.currentIndex)
        assertEquals(true, state.isNextButtonEnabled) // No input, button enabled

        coVerify { repository.getLessons() }
        coVerify { repository.markLessonStarted("1") }
    }

    @Test
    fun `loadLessons failure updates error state`() = runTest(testDispatcher) {
        val error = Exception("Network error")
        coEvery { repository.getLessons() } returns Result.failure(error)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Network error", state.errorMessage)
    }

    @Test
    fun `loadLessons with empty list sets isDone to true`() = runTest(testDispatcher) {
        coEvery { repository.getLessons() } returns Result.success(emptyList())

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isDone)
    }

    @Test
    fun `inputChanged updates input text and enables button when input provided`() = runTest(testDispatcher) {
        val lesson = createTestLesson(id = 1, hasInput = true)
        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.InputChanged("test"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("test", state.currentInputText)
        assertEquals(true, state.isNextButtonEnabled)
    }

    @Test
    fun `inputChanged disables button when input is empty for lesson with input`() = runTest(testDispatcher) {
        val lesson = createTestLesson(id = 1, hasInput = true)
        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.InputChanged(""))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("", state.currentInputText)
        assertEquals(false, state.isNextButtonEnabled)
    }

    @Test
    fun `checkAnswer with correct answer shows correct animation`() = runTest(testDispatcher) {
        val lesson1 = createTestLesson(
            id = 1,
            hasInput = true,
            content = listOf(
                LessonContentSegment(color = "#FFFFFF", text = "Hello "),
                LessonContentSegment(color = "#FFFFFF", text = "World"),
                LessonContentSegment(color = "#FFFFFF", text = "!")
            ),
            inputRange = LessonInputRange(startIndex = 6, endIndex = 11)
        )
        val lesson2 = createTestLesson(id = 2, hasInput = false)

        coEvery { repository.getLessons() } returns Result.success(listOf(lesson1, lesson2))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)
        coEvery { repository.markLessonCompleted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.InputChanged("World"))
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showCorrectAnimation)
        assertTrue(state.isCurrentLessonSolved)

        coVerify { repository.markLessonCompleted("1") }
    }

    @Test
    fun `checkAnswer with incorrect answer shows wrong animation`() = runTest(testDispatcher) {
        val lesson = createTestLesson(
            id = 1,
            hasInput = true,
            content = listOf(
                LessonContentSegment(color = "#FFFFFF", text = "Hello "),
                LessonContentSegment(color = "#FFFFFF", text = "World"),
                LessonContentSegment(color = "#FFFFFF", text = "!")
            ),
            inputRange = LessonInputRange(startIndex = 6, endIndex = 11)
        )

        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.InputChanged("Wrong"))
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showWrongAnimation)
        assertFalse(state.isCurrentLessonSolved)
    }

    @Test
    fun `checkAnswer for lesson without input marks as completed immediately`() = runTest(testDispatcher) {
        val lesson = createTestLesson(id = 1, hasInput = false)
        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)
        coEvery { repository.markLessonCompleted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isCurrentLessonSolved)

        coVerify { repository.markLessonCompleted("1") }
    }

    @Test
    fun `correctAnimationFinished advances to next lesson`() = runTest(testDispatcher) {
        val lessons = listOf(
            createTestLesson(id = 1, hasInput = false),
            createTestLesson(id = 2, hasInput = false)
        )

        coEvery { repository.getLessons() } returns Result.success(lessons)
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)
        coEvery { repository.markLessonCompleted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        viewModel.processIntent(Intent.CorrectAnimationFinished)
        advanceUntilIdle()

        // Check state directly
        val state = viewModel.state.value
        assertEquals(1, state.currentIndex)
        assertEquals("", state.currentInputText)
        assertFalse(state.isCurrentLessonSolved)

        coVerify { repository.markLessonStarted("2") }
    }

    @Test
    fun `correctAnimationFinished on last lesson sets isDone`() = runTest(testDispatcher) {
        val lesson = createTestLesson(id = 1, hasInput = false)
        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)
        coEvery { repository.markLessonCompleted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.processIntent(Intent.CorrectAnimationFinished)
            advanceUntilIdle()
            
            val effect = awaitItem()
            assertTrue(effect is Effect.NavigateToDone)
        }

        val state = viewModel.state.value
        assertTrue(state.isDone)
    }

    @Test
    fun `wrongAnimationFinished clears wrong animation flag`() = runTest(testDispatcher) {
        val lesson = createTestLesson(
            id = 1,
            hasInput = true,
            content = listOf(
                LessonContentSegment(color = "#FFFFFF", text = "Hello "),
                LessonContentSegment(color = "#FFFFFF", text = "World"),
                LessonContentSegment(color = "#FFFFFF", text = "!")
            ),
            inputRange = LessonInputRange(startIndex = 6, endIndex = 11)
        )

        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.processIntent(Intent.InputChanged("Wrong"))
        advanceUntilIdle()

        viewModel.processIntent(Intent.CheckAnswerClicked)
        advanceUntilIdle()

        viewModel.processIntent(Intent.WrongAnimationFinished)
        advanceUntilIdle()

        // Check state directly
        val state = viewModel.state.value
        assertFalse(state.showWrongAnimation)
    }

    @Test
    fun `nextLessonClicked without solving shows message`() = runTest(testDispatcher) {
        val lesson = createTestLesson(id = 1, hasInput = false)
        coEvery { repository.getLessons() } returns Result.success(listOf(lesson))
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.LoadLessons)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.processIntent(Intent.NextLessonClicked)
            advanceUntilIdle()
            
            val effect = awaitItem()
            assertTrue(effect is Effect.ShowMessage)
            assertEquals("Please solve the lesson first", (effect as Effect.ShowMessage).message)
        }
    }

    @Test
    fun `retry reloads lessons`() = runTest(testDispatcher) {
        val lessons = listOf(createTestLesson(id = 1, hasInput = false))
        coEvery { repository.getLessons() } returns Result.success(lessons)
        coEvery { repository.markLessonStarted(any()) } returns Result.success(Unit)

        viewModel.processIntent(Intent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getLessons() }
    }

    private fun createTestLesson(
        id: Int,
        hasInput: Boolean,
        content: List<LessonContentSegment> = listOf(
            LessonContentSegment(color = "#FFFFFF", text = "Test")
        ),
        inputRange: LessonInputRange? = if (hasInput) {
            LessonInputRange(startIndex = 0, endIndex = 4)
        } else {
            null
        }
    ): Lesson {
        return Lesson(
            id = id,
            content = content,
            input = inputRange
        )
    }
}

