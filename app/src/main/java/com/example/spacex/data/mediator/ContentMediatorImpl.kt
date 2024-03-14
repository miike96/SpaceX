package com.example.spacex.data.mediator

import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.data.database.repository.DataBaseRepository
import com.example.spacex.data.network.ApiManager
import com.example.spacex.utils.helper_classes.Failure
import com.example.spacex.utils.helper_classes.Logger
import com.example.spacex.utils.enums.SortingType
import com.example.spacex.utils.helper_classes.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

const val TAG = "ContentMediatorImpl"

class ContentMediatorImpl @Inject constructor(
    private val apiManager: ApiManager,
    private val dataBaseRepository: DataBaseRepository,
) : ContentMediator {

    // --- Updating the Database by calling our Api ---//
    override suspend fun getShipsDataFromApi() = flow {
        val allShips = apiManager.getAllShips()
        if (allShips is Success) {
            val shipsResult = allShips.data
            emit(shipsResult)
        } else if (allShips is Failure) {
            Logger.e(TAG, allShips.exc?.message ?: "Unknown Api Error")
        }
    }

    override suspend fun getLaunchesDataFromApi() = flow {
        val allLaunches = apiManager.getAllLaunches()
        if (allLaunches is Success) {
            val launchesResult = allLaunches.data
            emit(launchesResult)
        } else if (allLaunches is Failure) {
            Logger.e(TAG, allLaunches.exc?.message ?: "Unknown Api Error")
        }
    }

    override suspend fun updateDBFromApi() {
        // We are going to store the network results here
        val listOfLaunches = mutableListOf<Launch>()
        val listOfShips = mutableListOf<Ship>()

        //-------------------- Fetching the Launches Data --------------------//
        getLaunchesDataFromApi()
            .flowOn(Dispatchers.IO)
            // transforming the items inside the list to be single flows
            // now its easier to deal with them separately.
            .flatMapConcat { item -> item.asFlow() }
            // mapping the launch response to a ship DB entity model
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
            .collect { responses ->
                listOfLaunches.add(
                    responses
                )
            }

        //-------------------- Fetching the Ships Data --------------------//
        getShipsDataFromApi()
            .flowOn(Dispatchers.IO)
            // transforming the items inside the list to be single flows
            // now its easier to deal with them separately.
            .flatMapConcat { item -> item.asFlow() }
            // mapping the ship response to a ship DB entity model
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
            .collect { responses ->
                listOfShips.add(
                    responses
                )
            }
        // --- Adding the data into the Database --- //
        listOfShips.forEach { ship ->
            dataBaseRepository.addShip(ship)
        }
        listOfLaunches.forEach { launch ->
            dataBaseRepository.addLaunch(launch)
        }
    }

    // --- Retrieving data from the database ---//
    override suspend fun getShipsFromDB() = flow {
        dataBaseRepository.getAllShips()
            .flowOn(Dispatchers.IO)
            .collect { listOfShips ->
                emit(listOfShips)
            }
    }

    override suspend fun getShipsByMatchingName(searchQuery: String): Flow<List<Ship>> =
        dataBaseRepository.getShipsByMatchingName(searchQuery)

    override suspend fun getLaunchesFromDB(
        searchQuery: String,
        sortBy: SortingType
    ): Flow<List<Launch>> =
        dataBaseRepository.getAllLaunchesSorted(sortBy, searchQuery)

    override suspend fun getLaunchesByIds(launchIds: List<String>): Flow<List<Launch>> = flow {

        dataBaseRepository.getLaunchesById(launchIds)
            .flowOn(Dispatchers.IO)
            .collect { launches ->
                emit(launches)
            }
    }

    override suspend fun getShipsByIds(shipsIds: List<String>): Flow<List<Ship>> = flow {

        dataBaseRepository.getShipsById(shipsIds)
            .flowOn(Dispatchers.IO)
            .collect { ships ->
                emit(ships)
            }
    }

    // --- Checking If the Database has new data
    override suspend fun checkIfNewLaunchesAvailable() = flow {

        // Checking if the amount of launches in the DB is different than the amount we get from the API.
        val apiSize = getAmountOfLaunchesFromAPI()
        val dbSize = getAmountOfLaunchesInDB()

        // If the amount is different return true(new launches available ),
        // else return false (no new launches available)
        val result = apiSize.combine(dbSize) { api, db ->
            val thereIsDifference = (api != db)
            if (thereIsDifference){
                updateDBFromApi()
            }
           return@combine thereIsDifference
        }

       result.collect{
           emit(it)
       }
    }

    private suspend fun getAmountOfLaunchesFromAPI() = flow {

        // Getting All launches available from API
        getLaunchesDataFromApi()
            .flowOn(Dispatchers.IO)
            .collect {
                emit(it.size)
            }
    }

    private suspend fun getAmountOfLaunchesInDB() = flow {

        // Getting Launches from DB
        dataBaseRepository.getAllLaunches()
            .flowOn(Dispatchers.IO)
            .collect {
                emit(it.size)
            }
    }

}