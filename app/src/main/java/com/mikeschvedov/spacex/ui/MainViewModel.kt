package com.mikeschvedov.spacex.ui

import androidx.lifecycle.ViewModel
import com.mikeschvedov.spacex.utils.helper_classes.LiveNetworkMonitor
import com.mikeschvedov.spacex.utils.helper_classes.NetworkStatusChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val liveNetworkMonitor: LiveNetworkMonitor,
    val networkStatusChecker: NetworkStatusChecker
): ViewModel() {


}