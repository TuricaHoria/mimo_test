package com.example.mimotest.feature.lesson.data

import com.example.mimotest.feature.lesson.Lesson
import com.example.mimotest.feature.lesson.LessonContentSegment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LessonRepositoryTest {

    private lateinit var remoteDataSource: LessonRemoteDataSource
    private lateinit var completionStore: LessonCompletionStore
    private lateinit var repository: LessonRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = mockk()
        completionStore = mockk()
        repository = LessonRepositoryImpl(remoteDataSource, completionStore)
    }

    @Test
    fun `getLessons delegates to remote data source`() = runTest {
        val lessons = listOf(
            Lesson(
                id = 1,
                content = listOf(LessonContentSegment(color = "#FFFFFF", text = "Test")),
                input = null
            )
        )

        coEvery { remoteDataSource.fetchLessons() } returns Result.success(lessons)

        val result = repository.getLessons()

        assertTrue(result.isSuccess)
        assertEquals(lessons, result.getOrNull())
        coVerify { remoteDataSource.fetchLessons() }
    }

    @Test
    fun `getLessons propagates error from remote data source`() = runTest {
        val error = Exception("Network error")
        coEvery { remoteDataSource.fetchLessons() } returns Result.failure(error)

        val result = repository.getLessons()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `markLessonStarted with InMemoryLessonCompletionStore stores start time`() = runTest {
        val store = InMemoryLessonCompletionStore()
        val testRepository = LessonRepositoryImpl(remoteDataSource, store)
        val lessonId = "1"

        val result = testRepository.markLessonStarted(lessonId)

        assertTrue(result.isSuccess)
        val startTime = store.getLessonStartTime(lessonId)
        assertNotNull(startTime)
    }

    @Test
    fun `markLessonCompleted creates completion event with start and end times`() = runTest {
        val store = InMemoryLessonCompletionStore()
        val testRepository = LessonRepositoryImpl(remoteDataSource, store)
        val lessonId = "1"
        val startTime = System.currentTimeMillis()

        store.markLessonStarted(lessonId, startTime)
        Thread.sleep(10)
        val result = testRepository.markLessonCompleted(lessonId)

        assertTrue(result.isSuccess)
        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertTrue(completedIds?.contains(lessonId) == true)
    }

    @Test
    fun `markLessonCompleted uses current time as fallback when start time not found`() = runTest {
        val store = InMemoryLessonCompletionStore()
        val testRepository = LessonRepositoryImpl(remoteDataSource, store)
        val lessonId = "1"

        val result = testRepository.markLessonCompleted(lessonId)

        assertTrue(result.isSuccess)
        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertTrue(completedIds?.contains(lessonId) == true)
    }

    @Test
    fun `markLessonCompleted handles errors gracefully`() = runTest {
        val store = mockk<LessonCompletionStore>()
        val testRepository = LessonRepositoryImpl(remoteDataSource, store)
        val lessonId = "1"

        coEvery { store.saveCompletion(any()) } throws Exception("DB error")

        val result = testRepository.markLessonCompleted(lessonId)

        assertTrue(result.isFailure)
    }
}

