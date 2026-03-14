package com.afgalindob.todoapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.local.dao.TaskDao

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

/*
Para migraciones:

.addMigrations(MIGRATION_1_2)
 val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            "ALTER TABLE tasks ADD COLUMN createdAt INTEGER"
        )

    }
}
*/
