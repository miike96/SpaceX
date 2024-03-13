package com.mikeschvedov.spacex.data.network

import com.mikeschvedov.spacex.models.network.launches.LaunchesResponseWrapper
import com.mikeschvedov.spacex.models.network.ships.ShipResponse
import com.mikeschvedov.spacex.utils.helper_classes.Failure
import com.mikeschvedov.spacex.utils.helper_classes.ResultWrapper
import com.mikeschvedov.spacex.utils.helper_classes.Success
import javax.inject.Inject

class ApiManagerImpl @Inject constructor(
    private val spaceXApi: SpaceXApi,
) : ApiManager {

    // Success is a type of ResultWrapper so we are going to return it if all goes well
    // Failure is a type of ResultWrapper so we are going to return it if there is an exception
    override suspend fun getAllShips(): ResultWrapper<List<ShipResponse>> = try {
        Success(spaceXApi.getAllShips())
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun getAllLaunches(): ResultWrapper<LaunchesResponseWrapper> = try {
        Success(spaceXApi.getAllLaunches())
    } catch (e: Exception) {
        Failure(e)
    }
}