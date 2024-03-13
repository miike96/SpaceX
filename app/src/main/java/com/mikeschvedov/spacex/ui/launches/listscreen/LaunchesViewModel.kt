package com.mikeschvedov.spacex.ui.launches.listscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.mediator.ContentMediator
import com.mikeschvedov.spacex.utils.helper_classes.SingleEvent
import com.mikeschvedov.spacex.utils.enums.SortingType
import com.mikeschvedov.spacex.utils.adapters.LaunchesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) : ViewModel() {

    private val adapter = LaunchesAdapter() { launchCallback ->
        _selectedLaunch.postValue(SingleEvent(launchCallback))
    }

    private val _selectedLaunch = MutableLiveData<SingleEvent<Launch>>()
    val selectedLaunch: LiveData<SingleEvent<Launch>> get() = _selectedLaunch

    private val _listOfLaunches = MutableLiveData<List<Launch>>()
    val listOfLaunches: LiveData<List<Launch>> get() = _listOfLaunches

    fun searchInDB(searchQuery: String, sortBy: SortingType){

        viewModelScope.launch {
            contentMediator.getLaunchesFromDB(searchQuery, sortBy)
                .collect {
                    _listOfLaunches.postValue(it)
                }
        }
    }

    fun getLaunchesAdapter(): LaunchesAdapter {
        return adapter
    }
}