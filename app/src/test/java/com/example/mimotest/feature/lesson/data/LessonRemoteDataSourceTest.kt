package com.example.mimotest.feature.lesson.data

import com.example.mimotest.feature.lesson.Lesson
import com.example.mimotest.feature.lesson.LessonContentSegment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LessonRemoteDataSourceTest {

    private lateinit var apiService: LessonApiService
    private lateinit var dataSource: LessonRemoteDataSourceImpl

    @Before
    fun setup() {
        apiService = mockk()
        dataSource = LessonRemoteDataSourceImpl(apiService)
    }

    @Test
    fun `fetchLessons success returns lessons`() = runTest {
        val lessons = listOf(
            Lesson(
                id = 1,
                content = listOf(LessonContentSegment(color = "#FFFFFF", text = "Hello")),
                input = null
            ),
            Lesson(
                id = 2,
                content = listOf(LessonContentSegment(color = "#FFFFFF", text = "World")),
                input = null
            )
        )
        val response = LessonsResponse(lessons)

        coEvery { apiService.getLessons() } returns response

        val result = dataSource.fetchLessons()

        assertTrue(result.isSuccess)
        assertEquals(lessons, result.getOrNull())
        coVerify { apiService.getLessons() }
    }

    @Test
    fun `fetchLessons failure returns error`() = runTest {
        val error = Exception("Network error")
        coEvery { apiService.getLessons() } throws error

        val result = dataSource.fetchLessons()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `fetchLessons with empty list returns success`() = runTest {
        val response = LessonsResponse(emptyList())
        coEvery { apiService.getLessons() } returns response

        val result = dataSource.fetchLessons()

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Lesson>(), result.getOrNull())
    }
}

