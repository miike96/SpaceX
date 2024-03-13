package com.mikeschvedov.spacex.data.network

import com.mikeschvedov.spacex.models.network.launches.LaunchesResponseWrapper
import com.mikeschvedov.spacex.models.network.ships.ShipResponse
import com.mikeschvedov.spacex.models.network.ships.ShipsResponseWrapper
import retrofit2.http.GET

interface SpaceXApi {

    //https://api.spacexdata.com/v4/ships
    //https://api.spacexdata.com/v4/launches

    @GET("v4/launches")
    suspend fun getAllLaunches(): LaunchesResponseWrapper

    @GET("v4/ships")
    suspend fun getAllShips(): List<ShipResponse>

}
