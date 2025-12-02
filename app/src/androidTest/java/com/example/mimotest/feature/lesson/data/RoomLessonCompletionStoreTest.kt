package com.example.mimotest.feature.lesson.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomLessonCompletionStoreTest {

    private lateinit var database: LessonDatabase
    private lateinit var store: RoomLessonCompletionStore
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setup() {
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            context,
            LessonDatabase::class.java
        ).allowMainThreadQueries().build()
        
        // Override the database instance for testing
        LessonDatabase.setTestInstance(database)
        store = RoomLessonCompletionStore(context)
    }

    @After
    fun teardown() {
        database.close()
        LessonDatabase.clearTestInstance()
    }

    @Test
    fun `markLessonStarted stores start time`() = runTest {
        val lessonId = "1"
        val startTime = System.currentTimeMillis()

        store.markLessonStarted(lessonId, startTime)

        val retrievedTime = store.getLessonStartTime(lessonId)
        assertEquals(startTime, retrievedTime)
    }

    @Test
    fun `getLessonStartTime returns null for non-existent lesson`() = runTest {
        val retrievedTime = store.getLessonStartTime("999")
        assertNull(retrievedTime)
    }

    @Test
    fun `markLessonStarted replaces existing start time`() = runTest {
        val lessonId = "1"
        val firstTime = 1000L
        val secondTime = 2000L

        store.markLessonStarted(lessonId, firstTime)
        store.markLessonStarted(lessonId, secondTime)

        val retrievedTime = store.getLessonStartTime(lessonId)
        assertEquals(secondTime, retrievedTime)
    }

    @Test
    fun `saveCompletion stores completion event`() = runTest {
        val event = LessonCompletionEvent(
            lessonId = "1",
            startedAtMillis = 1000L,
            completedAtMillis = 2000L
        )

        val result = store.saveCompletion(event)

        assertTrue(result.isSuccess)
        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertTrue(completedIds?.contains("1") == true)
    }

    @Test
    fun `getCompletedLessonIds returns all completed lesson IDs`() = runTest {
        val event1 = LessonCompletionEvent(
            lessonId = "1",
            startedAtMillis = 1000L,
            completedAtMillis = 2000L
        )
        val event2 = LessonCompletionEvent(
            lessonId = "2",
            startedAtMillis = 2000L,
            completedAtMillis = 3000L
        )

        store.saveCompletion(event1)
        store.saveCompletion(event2)

        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertNotNull(completedIds)
        assertEquals(2, completedIds?.size)
        assertTrue(completedIds?.contains("1") == true)
        assertTrue(completedIds?.contains("2") == true)
    }

    @Test
    fun `getCompletedLessonIds returns empty set when no completions`() = runTest {
        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertNotNull(completedIds)
        assertTrue(completedIds?.isEmpty() == true)
    }

    @Test
    fun `saveCompletion replaces existing completion`() = runTest {
        val lessonId = "1"
        val firstEvent = LessonCompletionEvent(
            lessonId = lessonId,
            startedAtMillis = 1000L,
            completedAtMillis = 2000L
        )
        val secondEvent = LessonCompletionEvent(
            lessonId = lessonId,
            startedAtMillis = 2000L,
            completedAtMillis = 3000L
        )

        store.saveCompletion(firstEvent)
        store.saveCompletion(secondEvent)

        val completedIds = store.getCompletedLessonIds().getOrNull()
        assertEquals(1, completedIds?.size)
        assertTrue(completedIds?.contains(lessonId) == true)
    }
}

