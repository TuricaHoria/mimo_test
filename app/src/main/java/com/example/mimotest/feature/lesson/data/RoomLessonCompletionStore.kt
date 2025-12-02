package com.example.mimotest.feature.lesson.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RoomLessonCompletionStore(
    private val context: Context
) : LessonCompletionStore, LessonStartTimeStore {

    private val database: LessonDatabase by lazy {
        LessonDatabase.getDatabase(context)
    }

    private val dao: LessonDao by lazy {
        database.lessonDao()
    }

    override suspend fun markLessonStarted(lessonId: String, startTimeMillis: Long) {
        withContext(Dispatchers.IO) {
            dao.insertStartTime(
                LessonStartTimeEntity(
                    lessonId = lessonId,
                    startedAtMillis = startTimeMillis
                )
            )
        }
    }

    override suspend fun getLessonStartTime(lessonId: String): Long? {
        return withContext(Dispatchers.IO) {
            dao.getStartTime(lessonId)
        }
    }

    override suspend fun saveCompletion(event: LessonCompletionEvent): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dao.insertCompletion(
                    LessonCompletionEntity(
                        lessonId = event.lessonId,
                        startedAtMillis = event.startedAtMillis,
                        completedAtMillis = event.completedAtMillis
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCompletedLessonIds(): Result<Set<String>> {
        return try {
            val ids = withContext(Dispatchers.IO) {
                dao.getCompletedLessonIds()
            }
            Result.success(ids.toSet())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

