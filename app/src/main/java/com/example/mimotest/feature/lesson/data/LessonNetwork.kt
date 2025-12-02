package com.example.mimotest.feature.lesson.data

import android.util.Log
import com.example.mimotest.feature.lesson.Lesson
import retrofit2.http.GET


data class LessonsResponse(
    val lessons: List<Lesson>
)


interface LessonApiService {

    @GET("api/lessons")
    suspend fun getLessons(): LessonsResponse
}


class LessonRemoteDataSourceImpl(
    private val apiService: LessonApiService
) : LessonRemoteDataSource {

    override suspend fun fetchLessons(): Result<List<Lesson>> {
        return try {
            Log.d("LessonApi", "Requesting lessons…")
            val response = apiService.getLessons()
            Log.d(
                "LessonApi",
                "Received ${response.lessons.size} lessons: ${response.lessons.joinToString(limit = 3) { it.id.toString() }}"
            )
            Result.success(response.lessons)
        } catch (t: Throwable) {
            Log.e("LessonApi", "Error fetching lessons", t)
            Result.failure(t)
        }
    }
}


class InMemoryLessonCompletionStore : LessonCompletionStore, LessonStartTimeStore {
    private val completed = mutableSetOf<String>()
    private val lessonStartTimes = mutableMapOf<String, Long>()

    override suspend fun markLessonStarted(lessonId: String, startTimeMillis: Long) {
        lessonStartTimes[lessonId] = startTimeMillis
    }

    override suspend fun getLessonStartTime(lessonId: String): Long? {
        return lessonStartTimes[lessonId]
    }

    override suspend fun saveCompletion(event: LessonCompletionEvent): Result<Unit> {
        completed += event.lessonId
        return Result.success(Unit)
    }

    override suspend fun getCompletedLessonIds(): Result<Set<String>> {
        return Result.success(completed.toSet())
    }
}


class LessonRepositoryImpl(
    private val remoteDataSource: LessonRemoteDataSource,
    private val completionStore: LessonCompletionStore
) : LessonRepository {

    override suspend fun getLessons(): Result<List<Lesson>> {
        return remoteDataSource.fetchLessons()
    }

    override suspend fun markLessonStarted(lessonId: String): Result<Unit> {
        return try {
            val startTime = System.currentTimeMillis()
            (completionStore as? LessonStartTimeStore)?.markLessonStarted(lessonId, startTime)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markLessonCompleted(lessonId: String): Result<Unit> {
        return try {
            val completedAtMillis = System.currentTimeMillis()
            
            val startedAtMillis = (completionStore as? LessonStartTimeStore)
                ?.getLessonStartTime(lessonId)
                ?: completedAtMillis

            val event = LessonCompletionEvent(
                lessonId = lessonId,
                startedAtMillis = startedAtMillis,
                completedAtMillis = completedAtMillis
            )
            completionStore.saveCompletion(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


