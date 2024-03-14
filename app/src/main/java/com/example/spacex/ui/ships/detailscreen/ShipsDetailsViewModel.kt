package com.example.spacex.ui.ships.detailscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.mediator.ContentMediator
import com.example.spacex.utils.helper_classes.SingleEvent
import com.example.spacex.utils.adapters.MiniLaunchAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipsDetailsViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) : ViewModel() {

    private val adapter = MiniLaunchAdapter { launchCallback ->
        _selectedLaunch.postValue(SingleEvent(launchCallback))
    }

    private val _selectedLaunch = MutableLiveData<SingleEvent<Launch>>()
    val selectedLaunch: LiveData<SingleEvent<Launch>> get() = _selectedLaunch

    private val _listOfLaunches = MutableLiveData<List<Launch>>()
    val listOfLaunches: LiveData<List<Launch>> get() = _listOfLaunches

    fun getLaunchesByIds(launchesIds: List<String>) {
        viewModelScope.launch {
            contentMediator.getLaunchesByIds(launchesIds)
                .collect {
                    _listOfLaunches.postValue(it)
                }
        }
    }

    fun getAdapter(): MiniLaunchAdapter {
        return adapter
    }
}
