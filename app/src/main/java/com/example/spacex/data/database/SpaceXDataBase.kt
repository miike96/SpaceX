package com.example.spacex.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spacex.data.database.daos.LaunchesDao
import com.example.spacex.data.database.daos.ShipsDao
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.utils.converters.StringListConverter

@Database(entities = [Ship::class, Launch::class], version = 1)
@TypeConverters(StringListConverter::class)
abstract class SpaceXDataBase: RoomDatabase() {
    abstract fun shipsDao(): ShipsDao
    abstract fun launchesDao(): LaunchesDao
}