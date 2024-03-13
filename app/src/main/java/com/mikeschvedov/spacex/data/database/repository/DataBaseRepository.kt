package com.mikeschvedov.spacex.data.database.repository

import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.utils.enums.SortingType
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository {
    // --- Ships --- //
    suspend fun addShip(ship: Ship)
    suspend fun getAllShips(): Flow<List<Ship>>
    suspend fun getShipsByMatchingName(searchQuery:String) :  Flow<List<Ship>>
    suspend fun getShipsById(shipsIds: List<String>) : Flow<List<Ship>>
    // --- Launches --- //
    suspend fun addLaunch(launch: Launch)
    suspend fun getAllLaunchesSorted(sortBy: SortingType, searchQuery: String): Flow<List<Launch>>
    suspend fun getLaunchesById(launchIds: List<String>) : Flow<List<Launch>>
    suspend fun getAllLaunches(): Flow<List<Launch>>
  //  suspend fun getLaunchesByMatchingName(searchQuery:String) :  Flow<List<Launch>>

}