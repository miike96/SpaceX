package com.example.spacex.data.database.daos

import androidx.room.*
import com.example.spacex.data.database.entities.Ship
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShip(ship: Ship)

    @Query("SELECT * FROM ship ORDER BY name ASC ")
    fun getAllShipsOrderedByTitle() : Flow<List<Ship>>

    @Query("SELECT * FROM ship WHERE name LIKE '%' || :searchQuery || '%'")
    fun getShipsByMatchingName(searchQuery:String) : Flow<List<Ship>>

    // Get all Launches the match the id's in the list
    @Query("SELECT * FROM ship WHERE shipId IN (:shipsIDs)")
    fun getShipsByID(shipsIDs: List<String>) : Flow<List<Ship>>

}