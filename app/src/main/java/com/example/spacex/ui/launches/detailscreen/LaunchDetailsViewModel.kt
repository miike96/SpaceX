package com.example.spacex.ui.launches.detailscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.data.mediator.ContentMediator
import com.example.spacex.utils.helper_classes.SingleEvent
import com.example.spacex.utils.adapters.MiniShipAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailsViewModel @Inject constructor(
    private val contentMediator: ContentMediator
): ViewModel() {

    // Adapter for ships inside the Launch details page
    private val adapter = MiniShipAdapter { shipCallback ->
        _selectedShip.value = SingleEvent(shipCallback)
    }
    // LiveData for selected ship
    private val _selectedShip = MutableLiveData<SingleEvent<Ship>>()
    val selectedShip: LiveData<SingleEvent<Ship>> get() = _selectedShip

    private val _listOfShips = MutableLiveData<List<Ship>>()
    val listOfShips: LiveData<List<Ship>> get() = _listOfShips

    // Retrieve ships by their IDs
    fun getShipsByIds(shipsIds: List<String>) {
        viewModelScope.launch {
            contentMediator.getShipsByIds(shipsIds).collect {
                _listOfShips.value = it
            }
        }
    }

    // Provide the ship adapter
    fun getAdapter(): MiniShipAdapter {
        return adapter
    }
}
