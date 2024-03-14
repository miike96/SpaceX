package com.example.spacex.data.network

import com.example.spacex.models.network.launches.LaunchResponse
import com.example.spacex.models.network.ships.ShipResponse
import com.example.spacex.utils.helper_classes.ResultWrapper

interface ApiManager {
    suspend fun getAllShips(): ResultWrapper<List<ShipResponse>>
    suspend fun getAllLaunches(): ResultWrapper<List<LaunchResponse>>
}