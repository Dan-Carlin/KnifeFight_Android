package com.flounderguy.knifefightutilities.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flounderguy.knifefightutilities.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Gang::class, CharacterTrait::class], version = 1, exportSchema = false)
abstract class KnifeFightDatabase : RoomDatabase() {

    abstract fun gangDao(): GangDao

    abstract fun characterTraitDao(): CharacterTraitDao

    class Callback @Inject constructor(
        private val database: Provider<KnifeFightDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val characterTraitDao = database.get().characterTraitDao()

            applicationScope.launch {
                populateTraitTable(characterTraitDao)
            }
        }

        private suspend fun populateTraitTable(characterTraitDao: CharacterTraitDao) {
            characterTraitDao.deleteAll()

            characterTraitDao.insert(
                CharacterTrait(
                    "Brash",
                    "Gokujo knife",
                    0, 0, 1, 0, 0,
                    "Force enemy to re-roll 3x per game."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Burly",
                    "Stubby knife",
                    2, 0, 0, 0, 0,
                    "-1 dmg to all incoming dmg."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Fierce",
                    "Kukri",
                    0, 0, 0, 1, 0,
                    "Can counter attack on 4, 3, 2 or 1."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Heavy",
                    "Stilleto",
                    5, 1, 0, 0, 0,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Lucky",
                    "",
                    0, 0, 0, 0, 3,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Quick",
                    "Blinged Dagger",
                    0, 0, 1, 0, 1,
                    "Can counter a counter attack."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Practical",
                    "Pocket Knife",
                    0, 0, 0, 0, 0,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Unstable",
                    "Butterfly Knife",
                    -1, 0, 0, 0, 0,
                    "Call hit or miss before rolling-- +1d4 heal on miss or +1d4 dmg if hit."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Savage",
                    "Knife on Chain",
                    0, 0, 0, 1, 0,
                    "Roll above 16 or above?  Add 1d4 to dmg."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Smart",
                    "Digital Knife",
                    0, 0, 0, 0, 1,
                    "Re-roll 3x per game."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Strong",
                    "Knife on Weights",
                    2, 0, 0, 2, 0,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Slick",
                    "Toothpick Knife",
                    0, 0, 1, 0, 0,
                    "Win all ties, on a miss attack a different player."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Tenacious",
                    "Switch Blade",
                    3, 0, 0, 0, 1,
                    "Second Wind hits on 12 or above, come back to life with 1d6 HP."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Tough",
                    "Military Knife",
                    1, 3, 0, 0, 0,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Eager",
                    "Leather Dagger Knife",
                    -2, 0, 0, 0, 0,
                    "We are cheering for you."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Well-Rounded",
                    "Bowie Knife",
                    1, 1, 1, 1, 1,
                    "Most people like you."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Sketchy",
                    "Prison Shank",
                    0, 0, 0, 1, 0,
                    "atk any player for 1d4 dmg, anytime a player rolls 2, 10, or 19 (with atk die)."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Wily",
                    "Butcher Knife",
                    0, 0, 1, 0, 0,
                    "Roll a d20 after any player successfully attacks you.  If you roll" +
                            " higher than that person- direct the dmg to a player of your choosing."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Cocky",
                    "Comb Knife",
                    2, 0, 0, 0, 0,
                    "Heal 1d4 HP every successful attack"
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Aggressive",
                    "Rambo Knife",
                    1, 1, 0, 0, 0,
                    ""
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Adventurous",
                    "Whip Knife",
                    0, 0, 1, 0, 2,
                    "Can immediately grab a dropped item with his whip, Immune to Box of nails."
                )
            )
            characterTraitDao.insert(
                CharacterTrait(
                    "Low-Key",
                    "",
                    0, 0, 0, 1, 0,
                    "If you don't have an item in your hand when your turn ends, draw one item card."
                )
            )
        }
    }
}