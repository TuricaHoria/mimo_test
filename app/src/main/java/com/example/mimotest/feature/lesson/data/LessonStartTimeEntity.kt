package com.example.mimotest.feature.lesson.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "lesson_start_times")
data class LessonStartTimeEntity(
    @PrimaryKey
    val lessonId: String,
    val startedAtMillis: Long
)

