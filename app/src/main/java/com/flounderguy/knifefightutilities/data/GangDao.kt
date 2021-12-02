package com.flounderguy.knifefightutilities.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GangDao {

    @Query("SELECT * FROM gang_table WHERE isUser = 1")
    fun getUserGang(): Flow<Gang>

    @Query("SELECT * FROM gang_table WHERE isUser = 0")
    fun getRivalGangs(): Flow<List<Gang>>

    @Query("SELECT count(*) FROM gang_table WHERE isUser = 1")
    suspend fun gangCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gang: Gang)

    @Update
    suspend fun update(gang: Gang)

    @Query("DELETE FROM gang_table")
    suspend fun deleteAll()

}