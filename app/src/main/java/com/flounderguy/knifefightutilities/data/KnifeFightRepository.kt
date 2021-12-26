package com.flounderguy.knifefightutilities.data

import kotlinx.coroutines.flow.Flow

interface KnifeFightRepository {
    // Trait functions to implement
    fun getTraitList(): Flow<List<CharacterTrait>>
    fun getTraitFlow(traitLabel: Gang.Trait): Flow<CharacterTrait>
    suspend fun insertTrait(trait: CharacterTrait)
    suspend fun clearTraits()

    // Gang functions to implement
    fun getUserGang(): Flow<Gang>
    fun getRivalGangs(): Flow<List<Gang>>
    fun getGangNameFlow(): Flow<String>
    fun getGangColorFlow(): Flow<Gang.Color>
    fun getGangTraitFlow(): Flow<Gang.Trait>
    suspend fun getGangTrait(): Gang.Trait
    suspend fun userGangExists(): Boolean
    suspend fun rivalGangsExist(): Boolean
    suspend fun insertGang(gang: Gang)
    suspend fun updateGang(gang: Gang)
    suspend fun clearGangs()
}