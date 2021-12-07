package com.flounderguy.knifefightutilities.data

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KnifeFightDataRepository @Inject constructor(
    private val gangDao: GangDao,
    private val characterTraitDao: CharacterTraitDao
) : KnifeFightRepository {

    override fun getTraitList(): Flow<List<CharacterTrait>> {
        return characterTraitDao.getAll()
    }

    override fun getTraitFlow(traitLabel: Gang.Trait): Flow<CharacterTrait> {
        return characterTraitDao.getTraitByName(traitLabel.asString)
    }

    override suspend fun getHp(traitLabel: Gang.Trait): Int? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.hp
    }

    override suspend fun getGuts(traitLabel: Gang.Trait): Int? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.guts
    }

    override suspend fun getAttack(traitLabel: Gang.Trait): Int? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.attack
    }

    override suspend fun getDamage(traitLabel: Gang.Trait): Int? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.damage
    }

    override suspend fun getLuck(traitLabel: Gang.Trait): Int? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.luck
    }

    override suspend fun getWeapon(traitLabel: Gang.Trait): String? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.weapon
    }

    override suspend fun getUtility(traitLabel: Gang.Trait): String? {
        return characterTraitDao.getTraitByName(traitLabel.asString).asLiveData().value?.utility
    }

    override suspend fun insertTrait(trait: CharacterTrait) {
        characterTraitDao.insert(trait)
    }

    override suspend fun clearTraits() {
        characterTraitDao.deleteAll()
    }

    override fun getUserGang(): Flow<Gang> {
        return gangDao.getUserGang()
    }

    override fun getRivalGangs(): Flow<List<Gang>> {
        return gangDao.getRivalGangs()
    }

    override suspend fun getGangName(): String? {
        return if (userGangExists()) {
            getUserGang().asLiveData().value?.name
        } else {
            ""
        }
    }

    override suspend fun getGangColor(): Gang.Color? {
        return if (userGangExists()) {
            getUserGang().asLiveData().value?.color
        } else {
            Gang.Color.NONE
        }
    }

    override suspend fun getGangTrait(): Gang.Trait? {
        return if (userGangExists()) {
            getUserGang().asLiveData().value?.trait
        } else {
            Gang.Trait.NONE
        }
    }

    override suspend fun userGangExists(): Boolean {
        return gangDao.userGangCount() != 0
    }

    override suspend fun rivalGangsExist(): Boolean {
        return gangDao.rivalGangCount() != 0
    }

    override suspend fun insertGang(gang: Gang) {
        gangDao.insert(gang)
    }

    override suspend fun updateGang(gang: Gang) {
        gangDao.update(gang)
    }

    override suspend fun clearGangs() {
        gangDao.deleteAll()
    }
}

interface KnifeFightRepository {
    // Trait functions to implement
    fun getTraitList(): Flow<List<CharacterTrait>>
    fun getTraitFlow(traitLabel: Gang.Trait): Flow<CharacterTrait>
    suspend fun getHp(traitLabel: Gang.Trait): Int?
    suspend fun getGuts(traitLabel: Gang.Trait): Int?
    suspend fun getAttack(traitLabel: Gang.Trait): Int?
    suspend fun getDamage(traitLabel: Gang.Trait): Int?
    suspend fun getLuck(traitLabel: Gang.Trait): Int?
    suspend fun getWeapon(traitLabel: Gang.Trait): String?
    suspend fun getUtility(traitLabel: Gang.Trait): String?
    suspend fun insertTrait(trait: CharacterTrait)
    suspend fun clearTraits()

    // Gang functions to implement
    fun getUserGang(): Flow<Gang>
    fun getRivalGangs(): Flow<List<Gang>>
    suspend fun getGangName(): String?
    suspend fun getGangColor(): Gang.Color?
    suspend fun getGangTrait(): Gang.Trait?
    suspend fun userGangExists(): Boolean
    suspend fun rivalGangsExist(): Boolean
    suspend fun insertGang(gang: Gang)
    suspend fun updateGang(gang: Gang)
    suspend fun clearGangs()
}