package com.flounderguy.knifefightutilities.di

import android.app.Application
import androidx.room.Room
import com.flounderguy.knifefightutilities.data.KnifeFightDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KnifeFightAppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: KnifeFightDatabase.Callback
    ) = Room.databaseBuilder(app, KnifeFightDatabase::class.java, "knife_fight_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    // TODO: Replace hard coded value with user input from the settings page.
    fun provideStartingHp() = 25

    @Provides
    fun provideGangDao(db: KnifeFightDatabase) = db.gangDao()

    @Provides
    fun provideCharacterTraitDao(db: KnifeFightDatabase) = db.characterTraitDao()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope