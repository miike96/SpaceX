package com.mikeschvedov.spacex.utils.helper_classes

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import javax.inject.Inject

/*
*  === How to use ===
*  inject the liveInternetChecker into the MainActivity's ViewModel
*  So make sure LiveInternetChecker has the @Inject constructor annotation (as we see here)
* then create an observer in the main activity : mainViewModel.liveInternetChecker.observe(this, {isNetworkAvailable ->
*   })
* */
// @Inject constructor(private val connectivityManager: ConnectivityManager)
class LiveNetworkMonitor@Inject constructor(private val connectivityManager: ConnectivityManager) : LiveData<Boolean>()  {

    // We Create a network callback
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    // A set of valid networks
    private val networksThatHaveInternet: MutableSet<Network> = HashSet()
    // If we have at least one valid network (with internet connection) send the LiveData boolean as True
    private fun checkIfOneNetworkHasInternet(){
        postValue(networksThatHaveInternet.size > 0)
    }

    // A LiveData method that is triggered when there are observers to this class
    // So it is a perfect place to register to the callback
    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // A LiveData method that is triggered when there are NO observers to this class
    // So it is a perfect place to unregister from the callback
    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object  : ConnectivityManager.NetworkCallback(){

        override fun onAvailable(network: Network) {
            println("Device got connection to this network: $network")
            // We check if this network actually has internet (it is not enough that it's just available)
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isInternet = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            // If this network has internet connection add it to the list
            if(isInternet == true){
                networksThatHaveInternet.add(network)
            }
            // We check if there is a valid network
            checkIfOneNetworkHasInternet()
        }

        override fun onLost(network: Network) {
            println("Device lost connection to this network: $network")
            // So we remove it from the valid networks list
            networksThatHaveInternet.remove(network)
            // We check if there is a valid network
            checkIfOneNetworkHasInternet()
        }
    }
}