package com.flounderguy.knifefightutilities.data

import android.util.Log
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
        Log.d("Repository", "getTraitFlow() called.")
        return characterTraitDao.getTraitByName(traitLabel.asString)
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

    override fun getGangNameFlow(): Flow<String> {
        return gangDao.getGangNameFlow()
    }

    override fun getGangColorFlow(): Flow<Gang.Color> {
        return gangDao.getGangColorFlow()
    }

    override fun getGangTraitFlow(): Flow<Gang.Trait> {
        return gangDao.getGangTraitFlow()
    }

    override suspend fun getGangTrait(): Gang.Trait {
        return gangDao.getGangTrait()
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