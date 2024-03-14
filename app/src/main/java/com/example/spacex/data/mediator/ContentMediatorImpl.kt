package com.example.spacex.data.mediator

import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.data.database.repository.DataBaseRepository
import com.example.spacex.data.network.ApiManager
import com.example.spacex.models.network.launches.LaunchResponse
import com.example.spacex.models.network.ships.ShipResponse
import com.example.spacex.utils.helper_classes.Failure
import com.example.spacex.utils.helper_classes.Logger
import com.example.spacex.utils.helper_classes.Success
import com.example.spacex.utils.enums.SortingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

const val TAG = "ContentMediatorImpl"

class ContentMediatorImpl @Inject constructor(
    private val apiManager: ApiManager,
    private val dataBaseRepository: DataBaseRepository
) : ContentMediator {

    override suspend fun getShipsDataFromApi(): Flow<List<ShipResponse>> =
        flow {
            when (val allShips = apiManager.getAllShips()) {
                is Success -> emit(allShips.data)
                is Failure -> Logger.e(TAG, allShips.exc?.message ?: "Unknown Api Error")
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getLaunchesDataFromApi(): Flow<List<LaunchResponse>> =
        flow {
            when (val allLaunches = apiManager.getAllLaunches()) {
                is Success -> emit(allLaunches.data)
                is Failure -> Logger.e(TAG, allLaunches.exc?.message ?: "Unknown Api Error")
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun updateDBFromApi() {
        val listOfLaunches = mutableListOf<Launch>()
        val listOfShips = mutableListOf<Ship>()

        getLaunchesDataFromApi()
            .flatMapConcat { it.asFlow() }
            .map { launchResponse ->
                Launch(
                    launchResponse.id,
                    launchResponse.details,
                    launchResponse.flight_number,
                    launchResponse.date_unix.toLong(),
                    launchResponse.name,
                    launchResponse.links,
                    launchResponse.ships,
                    launchResponse.success
                )
            }
            .toList(listOfLaunches)

        getShipsDataFromApi()
            .flatMapConcat { it.asFlow() }
            .map { shipResponse ->
                Ship(
                    shipResponse.id,
                    shipResponse.image,
                    shipResponse.launches,
                    shipResponse.legacy_id,
                    shipResponse.model,
                    shipResponse.name,
                    shipResponse.status,
                    shipResponse.type,
                    shipResponse.year_built
                )
            }
            .toList(listOfShips)

        listOfShips.forEach { ship ->
            dataBaseRepository.addShip(ship)
        }
        listOfLaunches.forEach { launch ->
            dataBaseRepository.addLaunch(launch)
        }
    }

    override suspend fun getShipsFromDB(): Flow<List<Ship>> =
        dataBaseRepository.getAllShips().flowOn(Dispatchers.IO)

    override suspend fun getShipsByMatchingName(searchQuery: String): Flow<List<Ship>> =
        dataBaseRepository.getShipsByMatchingName(searchQuery)

    override suspend fun getLaunchesFromDB(searchQuery: String, sortBy: SortingType): Flow<List<Launch>> =
        dataBaseRepository.getAllLaunchesSorted(sortBy, searchQuery)

    override suspend fun getLaunchesByIds(launchIds: List<String>): Flow<List<Launch>> =
        dataBaseRepository.getLaunchesById(launchIds).flowOn(Dispatchers.IO)

    override suspend fun getShipsByIds(shipsIds: List<String>): Flow<List<Ship>> =
        dataBaseRepository.getShipsById(shipsIds).flowOn(Dispatchers.IO)

    override suspend fun checkIfNewLaunchesAvailable(): Flow<Boolean> =
        getAmountOfLaunchesFromAPI().combine(getAmountOfLaunchesInDB()) { api, db ->
            val thereIsDifference = (api != db)
            if (thereIsDifference) {
                updateDBFromApi()
            }
            thereIsDifference
        }.flowOn(Dispatchers.IO)

    private suspend fun getAmountOfLaunchesFromAPI(): Flow<Int> =
        getLaunchesDataFromApi().map { it.size }

    private suspend fun getAmountOfLaunchesInDB(): Flow<Int> =
        dataBaseRepository.getAllLaunches().map { it.size }

}
