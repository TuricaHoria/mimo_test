package com.example.mimotest.feature.lesson.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LessonDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStartTime(startTime: LessonStartTimeEntity)
    
    @Query("SELECT startedAtMillis FROM lesson_start_times WHERE lessonId = :lessonId")
    suspend fun getStartTime(lessonId: String): Long?
    
    @Query("SELECT * FROM lesson_start_times")
    suspend fun getAllStartTimes(): List<LessonStartTimeEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: LessonCompletionEntity)
    
    @Query("SELECT lessonId FROM lesson_completions")
    suspend fun getCompletedLessonIds(): List<String>
    
    @Query("SELECT * FROM lesson_completions")
    suspend fun getAllCompletions(): List<LessonCompletionEntity>
}

