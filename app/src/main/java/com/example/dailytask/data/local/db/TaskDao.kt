package com.example.dailytasks.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE dateStart < :dayEnd AND dateFinish > :dayStart ORDER BY dateStart")
    fun observeTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    @Query("SELECT MAX(id) FROM tasks")
    suspend fun getMaxId(): Long?

    @androidx.room.Update
    suspend fun update(task: TaskEntity)

    @androidx.room.Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

