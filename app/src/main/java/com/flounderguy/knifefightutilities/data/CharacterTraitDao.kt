package com.flounderguy.knifefightutilities.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterTraitDao {

    @Query("SELECT * FROM trait_table WHERE name IS :name")
    fun getTraitByName(name: String): Flow<CharacterTrait>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trait: CharacterTrait)

    @Query("DELETE FROM trait_table")
    suspend fun deleteAll()

}