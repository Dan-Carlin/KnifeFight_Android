package com.flounderguy.knifefightutilities.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GangDao {

    @Query("SELECT * FROM gang_table WHERE id IS 0")
    fun getCurrentGang(): Flow<Gang>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gang: Gang)

    @Update
    suspend fun update(gang: Gang)

    @Query("DELETE FROM gang_table")
    suspend fun delete()

}