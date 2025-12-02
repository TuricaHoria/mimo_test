package com.example.mimotest.feature.lesson.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseDebugHelper {
    private const val TAG = "DatabaseDebug"

    fun logDatabaseContents(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = LessonDatabase.getDatabase(context)
                val dao = database.lessonDao()
                
                val startTimes = dao.getAllStartTimes()
                Log.d(TAG, "=== LESSON START TIMES ===")
                if (startTimes.isEmpty()) {
                    Log.d(TAG, "No start times found")
                } else {
                    startTimes.forEach { entity ->
                        Log.d(TAG, "Lesson ID: ${entity.lessonId}, Started At: ${entity.startedAtMillis} (${java.util.Date(entity.startedAtMillis)})")
                    }
                }
                
                val completions = dao.getAllCompletions()
                Log.d(TAG, "\n=== LESSON COMPLETIONS ===")
                if (completions.isEmpty()) {
                    Log.d(TAG, "No completions found")
                } else {
                    completions.forEach { entity ->
                        val duration = entity.completedAtMillis - entity.startedAtMillis
                        Log.d(TAG, "Lesson ID: ${entity.lessonId}")
                        Log.d(TAG, "  Started: ${entity.startedAtMillis} (${java.util.Date(entity.startedAtMillis)})")
                        Log.d(TAG, "  Completed: ${entity.completedAtMillis} (${java.util.Date(entity.completedAtMillis)})")
                        Log.d(TAG, "  Duration: ${duration}ms (${duration / 1000.0}s)")
                        Log.d(TAG, "---")
                    }
                }
                
                Log.d(TAG, "\n=== SUMMARY ===")
                Log.d(TAG, "Total lessons started: ${startTimes.size}")
                Log.d(TAG, "Total lessons completed: ${completions.size}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error reading database", e)
            }
        }
    }
}

