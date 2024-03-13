package com.mikeschvedov.spacex.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mikeschvedov.spacex.data.database.daos.LaunchesDao
import com.mikeschvedov.spacex.data.database.daos.ShipsDao
import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.models.network.launches.Links
import com.mikeschvedov.spacex.utils.converters.LinksConverter
import com.mikeschvedov.spacex.utils.converters.StringListConverter

@Database(entities = [Ship::class, Launch::class], version = 1)
@TypeConverters(StringListConverter::class)
abstract class SpaceXDataBase: RoomDatabase() {
    abstract fun shipsDao(): ShipsDao
    abstract fun launchesDao(): LaunchesDao
}