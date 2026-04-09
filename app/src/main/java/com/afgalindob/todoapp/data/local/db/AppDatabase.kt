package com.afgalindob.todoapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.local.entity.NoteEntity
import com.afgalindob.todoapp.data.local.dao.TaskDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.afgalindob.todoapp.data.local.dao.NoteDao
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@Database(
    entities = [TaskEntity::class, NoteEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao

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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5
                    )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE tasks_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                date TEXT NOT NULL,
                completed INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """
        )

        val cursor = db.query("SELECT id, `values` FROM tasks")

        while (cursor.moveToNext()) {

            val id = cursor.getLong(0)
            val json = cursor.getString(1)

            val jsonObject = JSONObject(json)

            val title = jsonObject.optString("title", "")
            val description = jsonObject.optString("description", "")
            val date = jsonObject.optString("date", "")

            val completedString = jsonObject.optString("completed", "false")
            val completed = if (completedString == "true") 1 else 0

            val now = System.currentTimeMillis()

            db.execSQL(
                """
                INSERT INTO tasks_new
                (id, title, description, date, completed, createdAt, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                arrayOf(
                    id,
                    title,
                    description,
                    date,
                    completed,
                    now,
                    now
                )
            )
        }

        cursor.close()

        db.execSQL("DROP TABLE tasks")
        db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")

        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_completed ON tasks(completed)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_date ON tasks(date)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3){

    private fun parseDateToTimestamp(date: String?): Long {
        if (date.isNullOrEmpty()) return 0L

        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .parse(date)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE tasks_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                date INTEGER NOT NULL,
                completed INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """
        )
        val cursor = db.query("SELECT id, title, description, date, completed, createdAt, updatedAt FROM tasks")

        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            val dateString = cursor.getString(3)
            val completed = cursor.getInt(4)
            val createdAt = cursor.getLong(5)
            val updatedAt = cursor.getLong(6)

            val date = parseDateToTimestamp(dateString)

            db.execSQL(
                """
                INSERT INTO tasks_new
                (id, title, content, date, completed, createdAt, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                arrayOf(
                    id,
                    title,
                    description, // aquí haces rename → content
                    date,
                    completed,
                    createdAt,
                    updatedAt
                )
            )

        }

        cursor.close()

        db.execSQL("DROP TABLE tasks")
        db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")

        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_date ON tasks(date)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_completed ON tasks(completed)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `notes` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL, 
                `content` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
                )
            """
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Añadir columna a notes
        db.execSQL("ALTER TABLE `notes` ADD COLUMN `deleteAt` INTEGER NOT NULL DEFAULT 0")

        // Añadir columna a tasks
        db.execSQL("ALTER TABLE `tasks` ADD COLUMN `deleteAt` INTEGER NOT NULL DEFAULT 0")
    }
}