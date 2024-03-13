package com.mikeschvedov.spacex.data.network

import com.mikeschvedov.spacex.models.network.launches.LaunchResponse
import com.mikeschvedov.spacex.models.network.ships.ShipResponse
import com.mikeschvedov.spacex.utils.helper_classes.ResultWrapper

interface ApiManager {
    suspend fun getAllShips(): ResultWrapper<List<ShipResponse>>
    suspend fun getAllLaunches(): ResultWrapper<List<LaunchResponse>>
}