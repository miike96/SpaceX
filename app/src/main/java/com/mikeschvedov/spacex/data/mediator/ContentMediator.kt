package com.mikeschvedov.spacex.data.mediator

import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.models.network.launches.LaunchResponse
import com.mikeschvedov.spacex.models.network.ships.ShipResponse
import com.mikeschvedov.spacex.utils.enums.SortingType
import kotlinx.coroutines.flow.Flow

interface ContentMediator {

    // --- Updating the Database by calling our Api ---//
    suspend fun getShipsDataFromApi() :  Flow< List<ShipResponse>>
    suspend fun getLaunchesDataFromApi() : Flow<List<LaunchResponse>>
    suspend fun updateDBFromApi()

    // --- Retrieving data from the database ---//

    // -- Ships -- //
    suspend fun  getShipsFromDB(): Flow<List<Ship>>
   suspend fun getShipsByIds(shipsIds: List<String>): Flow<List<Ship>>
    suspend fun getShipsByMatchingName(searchQuery:String) :  Flow<List<Ship>>

    // -- Launches -- //
    suspend fun  getLaunchesFromDB(searchQuery: String, sortBy: SortingType) : Flow<List<Launch>>
    suspend fun  getLaunchesByIds(launchIds: List<String>): Flow<List<Launch>>

    // --- Checking If the Database has new data
    suspend fun checkIfNewLaunchesAvailable() : Flow<Boolean>

}