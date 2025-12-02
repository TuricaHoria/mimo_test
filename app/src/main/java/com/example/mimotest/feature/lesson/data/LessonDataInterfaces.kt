package com.example.mimotest.feature.lesson.data

import com.example.mimotest.feature.lesson.Lesson

interface LessonRemoteDataSource {
    suspend fun fetchLessons(): Result<List<Lesson>>
}

data class LessonCompletionEvent(
    val lessonId: String,
    val startedAtMillis: Long,
    val completedAtMillis: Long
)

interface LessonCompletionStore {
    suspend fun saveCompletion(event: LessonCompletionEvent): Result<Unit>
    suspend fun getCompletedLessonIds(): Result<Set<String>>
}

interface LessonStartTimeStore {
    suspend fun markLessonStarted(lessonId: String, startTimeMillis: Long)
    suspend fun getLessonStartTime(lessonId: String): Long?
}

interface LessonRepository {
    suspend fun getLessons(): Result<List<Lesson>>
    suspend fun markLessonStarted(lessonId: String): Result<Unit>
    suspend fun markLessonCompleted(lessonId: String): Result<Unit>
}


