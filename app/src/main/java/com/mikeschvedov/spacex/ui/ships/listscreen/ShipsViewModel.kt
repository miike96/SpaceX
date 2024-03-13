package com.mikeschvedov.spacex.ui.ships.listscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.data.mediator.ContentMediator
import com.mikeschvedov.spacex.utils.helper_classes.SingleEvent
import com.mikeschvedov.spacex.utils.adapters.ShipsAdapter
import com.mikeschvedov.spacex.utils.helper_classes.NetworkStatusChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipsViewModel @Inject constructor(
    private val contentMediator: ContentMediator,
    val networkStatusChecker: NetworkStatusChecker,
) : ViewModel() {

    private val adapter = ShipsAdapter() { shipCallback ->
        _selectedShip.postValue(SingleEvent(shipCallback))
    }

    private val _listOfShips = MutableLiveData<List<Ship>>()
    val listOfShips: LiveData<List<Ship>> get() = _listOfShips

    private val _selectedShip = MutableLiveData<SingleEvent<Ship>>()
    val selectedShip: LiveData<SingleEvent<Ship>> get() = _selectedShip

    // TODO this should probably be in the main activity's view model
    fun updateDB() {
        viewModelScope.launch {
            contentMediator.updateDBFromApi()
        }
    }


    fun searchInDB(searchQuery: String) {
        viewModelScope.launch {
            contentMediator.getShipsByMatchingName(searchQuery)
                .collect {
                    _listOfShips.postValue(it)
                }
        }
    }

    fun populateUI() {
        viewModelScope.launch {
            contentMediator.getShipsFromDB()
                .collect {
                    _listOfShips.postValue(it)
                }
        }
    }


    fun getShipsAdapter(): ShipsAdapter {
        return adapter
    }
}