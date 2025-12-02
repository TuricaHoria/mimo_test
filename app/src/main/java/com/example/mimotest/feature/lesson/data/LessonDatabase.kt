package com.example.mimotest.feature.lesson.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [LessonCompletionEntity::class, LessonStartTimeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LessonDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var INSTANCE: LessonDatabase? = null
        
        @Volatile
        private var TEST_INSTANCE: LessonDatabase? = null

        fun getDatabase(context: Context): LessonDatabase {
            TEST_INSTANCE?.let { return it }
            
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LessonDatabase::class.java,
                    "lesson_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
        
        fun setTestInstance(instance: LessonDatabase) {
            TEST_INSTANCE = instance
        }
        
        fun clearTestInstance() {
            TEST_INSTANCE = null
        }
    }
}

