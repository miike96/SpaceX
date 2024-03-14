package com.example.spacex.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spacex.data.database.entities.Launch
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLaunch(launch: Launch)

    @Query("SELECT * FROM launch WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC ")
    fun getAllLaunchesOrderedByTitle(searchQuery: String): Flow<List<Launch>>

    @Query("SELECT * FROM launch WHERE name LIKE '%' || :searchQuery || '%' ORDER BY date_unix ASC")
    fun getAllLaunchesOrderedByDateASC(searchQuery: String): Flow<List<Launch>>

    @Query("SELECT * FROM launch WHERE name LIKE '%' || :searchQuery || '%' ORDER BY date_unix DESC")
    fun getAllLaunchesOrderedByDateDESC(searchQuery: String): Flow<List<Launch>>

    // Get all Launches the match the id's in the list
    @Query("SELECT * FROM launch WHERE launchId IN (:launchIDs)")
    fun getLaunchesByID(launchIDs: List<String>) : Flow<List<Launch>>

    @Query("SELECT * FROM launch")
    fun getAllLaunches() : Flow<List<Launch>>

}