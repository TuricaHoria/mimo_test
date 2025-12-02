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
class LessonDaoTest {

    private lateinit var database: LessonDatabase
    private lateinit var dao: LessonDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LessonDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.lessonDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insertStartTime and getStartTime work correctly`() = runTest {
        val lessonId = "1"
        val startTime = System.currentTimeMillis()
        val entity = LessonStartTimeEntity(lessonId = lessonId, startedAtMillis = startTime)

        dao.insertStartTime(entity)

        val retrievedTime = dao.getStartTime(lessonId)
        assertEquals(startTime, retrievedTime)
    }

    @Test
    fun `getStartTime returns null for non-existent lesson`() = runTest {
        val retrievedTime = dao.getStartTime("999")
        assertNull(retrievedTime)
    }

    @Test
    fun `insertStartTime replaces existing start time`() = runTest {
        val lessonId = "1"
        val firstTime = 1000L
        val secondTime = 2000L

        dao.insertStartTime(LessonStartTimeEntity(lessonId, firstTime))
        dao.insertStartTime(LessonStartTimeEntity(lessonId, secondTime))

        val retrievedTime = dao.getStartTime(lessonId)
        assertEquals(secondTime, retrievedTime)
    }

    @Test
    fun `insertCompletion and getCompletedLessonIds work correctly`() = runTest {
        val event1 = LessonCompletionEntity(
            lessonId = "1",
            startedAtMillis = 1000L,
            completedAtMillis = 2000L
        )
        val event2 = LessonCompletionEntity(
            lessonId = "2",
            startedAtMillis = 2000L,
            completedAtMillis = 3000L
        )

        dao.insertCompletion(event1)
        dao.insertCompletion(event2)

        val completedIds = dao.getCompletedLessonIds()
        assertEquals(2, completedIds.size)
        assertTrue(completedIds.contains("1"))
        assertTrue(completedIds.contains("2"))
    }

    @Test
    fun `insertCompletion replaces existing completion`() = runTest {
        val lessonId = "1"
        val firstEvent = LessonCompletionEntity(
            lessonId = lessonId,
            startedAtMillis = 1000L,
            completedAtMillis = 2000L
        )
        val secondEvent = LessonCompletionEntity(
            lessonId = lessonId,
            startedAtMillis = 2000L,
            completedAtMillis = 3000L
        )

        dao.insertCompletion(firstEvent)
        dao.insertCompletion(secondEvent)

        val completedIds = dao.getCompletedLessonIds()
        assertEquals(1, completedIds.size)
        assertTrue(completedIds.contains(lessonId))
    }

    @Test
    fun `getCompletedLessonIds returns empty list when no completions`() = runTest {
        val completedIds = dao.getCompletedLessonIds()
        assertTrue(completedIds.isEmpty())
    }
}

