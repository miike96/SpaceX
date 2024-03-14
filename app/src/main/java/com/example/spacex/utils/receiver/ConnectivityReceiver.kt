package com.example.spacex.utils.receiver


/*
 ----------------------- CAN BE DELETED -----------------------

@AndroidEntryPoint
class ConnectivityReceiver: BroadcastReceiver() {

    @Inject
    lateinit var networkStatusChecker: NetworkStatusChecker

    // ConnectivityManager is sending us a broadcast each time there is a change in the network
    override fun onReceive(context: Context?, intent: Intent?) {
        println("A NETWORK CHANGE HAS OCCURRED")
        // If someone is listening to this receiver
        if (connectivityReceiverListener != null) {
            // Send them a callback of type Boolean "isConnected" that we get from NetworkStatusChecker
            // NetworkStatusChecker actually checks if there is any internet connection at all. (Now that we know something changed in the network)
            connectivityReceiverListener!!.onNetworkConnectionChanged(networkStatusChecker.hasInternetConnection())
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        // The filter we are going to register with, this receiver will only accept intents that relate to network changes.
       // IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        const val NETWORK_FILTER = "android.net.conn.CONNECTIVITY_CHANGE"
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }


}*/
