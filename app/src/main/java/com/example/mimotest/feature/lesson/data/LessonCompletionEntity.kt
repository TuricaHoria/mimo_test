package com.example.mimotest.feature.lesson.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_completions")
data class LessonCompletionEntity(
    @PrimaryKey
    val lessonId: String,
    val startedAtMillis: Long,
    val completedAtMillis: Long
)

