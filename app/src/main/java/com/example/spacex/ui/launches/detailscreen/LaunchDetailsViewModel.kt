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

    // We are using a Ship adapter inside the Launch details page
    private val adapter = MiniShipAdapter() { shipCallback ->
        _selectedShip.postValue(SingleEvent(shipCallback))
    }
    // When we click on a ship inside the details page
    private val _selectedShip = MutableLiveData<SingleEvent<Ship>>()
    val selectedShip: LiveData<SingleEvent<Ship>> get() = _selectedShip

    private val _listOfShips = MutableLiveData<List<Ship>>()
    val listOfShips: LiveData<List<Ship>> get() = _listOfShips

    fun getShipsByIds(shipsIds: List<String>) {
        viewModelScope.launch {
            contentMediator.getShipsByIds(shipsIds)
                .collect {
                    _listOfShips.postValue(it)
                }
        }
    }

    fun getAdapter(): MiniShipAdapter {
        return adapter
    }
}