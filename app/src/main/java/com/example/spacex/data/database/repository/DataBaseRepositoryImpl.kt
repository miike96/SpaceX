package com.example.spacex.data.database.repository

import com.example.spacex.data.database.daos.LaunchesDao
import com.example.spacex.data.database.daos.ShipsDao
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.utils.enums.SortingType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataBaseRepositoryImpl @Inject constructor(
    private val shipsDao: ShipsDao,
    private val launchesDao: LaunchesDao
) : DataBaseRepository {
    // ---- Ship ---- //
    override suspend fun addShip(ship: Ship) = shipsDao.addShip(ship)
    override suspend fun getAllShips(): Flow<List<Ship>> = shipsDao.getAllShipsOrderedByTitle()
    override suspend fun getShipsById(shipsIds: List<String>) : Flow<List<Ship>> =
        shipsDao.getShipsByID(shipsIds)
    override suspend fun getShipsByMatchingName(searchQuery: String): Flow<List<Ship>> =
        shipsDao.getShipsByMatchingName(searchQuery)

    // ---- Launch ---- //
    override suspend fun addLaunch(launch: Launch) = launchesDao.addLaunch(launch)

    override suspend fun getAllLaunchesSorted(sortBy: SortingType, searchQuery: String): Flow<List<Launch>> {
        return when (sortBy) {
            SortingType.ByDateASC -> launchesDao.getAllLaunchesOrderedByDateASC(searchQuery)
            SortingType.ByDateDESC -> launchesDao.getAllLaunchesOrderedByDateDESC(searchQuery)
            else -> launchesDao.getAllLaunchesOrderedByTitle(searchQuery)
        }
    }
    override suspend fun getLaunchesById(launchIds: List<String>) : Flow<List<Launch>> =
        launchesDao.getLaunchesByID(launchIds)

    override suspend fun getAllLaunches(): Flow<List<Launch>> = launchesDao.getAllLaunches()

}