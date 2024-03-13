package com.mikeschvedov.spacex.di

import android.content.Context
import androidx.room.Room
import com.mikeschvedov.spacex.data.database.SpaceXDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DBName = "SpaceXDB"

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(appContext, SpaceXDataBase::class.java, DBName)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideShipsDao(spaceXDB: SpaceXDataBase) =
        spaceXDB.shipsDao()

    @Provides
    fun provideLaunchesDao(spaceXDB: SpaceXDataBase) =
        spaceXDB.launchesDao()
}