package com.afgalindob.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("""
        SELECT * FROM tasks 
        WHERE 
            date >= :today 
            OR (:showCompleted OR completed = 0)
            AND deleteAt = 0
        ORDER BY date ASC
    """)
    fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertTask(task: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE notes SET deleteAt = :timestamp WHERE id = :id")
    suspend fun setOnDeleteTask(id: Long, timestamp: Long)

}