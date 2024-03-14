package com.example.spacex.utils.helper_classes

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import javax.inject.Inject

class LiveNetworkMonitor @Inject constructor(private val connectivityManager: ConnectivityManager) :
    LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val networksThatHaveInternet: MutableSet<Network> = HashSet()

    private fun checkIfOneNetworkHasInternet() {
        postValue(networksThatHaveInternet.isNotEmpty())
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (isInternet == true) {
                networksThatHaveInternet.add(network)
            }
            checkIfOneNetworkHasInternet()
        }

        override fun onLost(network: Network) {
            networksThatHaveInternet.remove(network)
            checkIfOneNetworkHasInternet()
        }
    }
}
