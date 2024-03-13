package com.mikeschvedov.spacex.ui.ships.listscreen

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.spacex.R
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.databinding.FragmentShipsBinding
import com.mikeschvedov.spacex.utils.helper_classes.SingleEvent
import com.mikeschvedov.spacex.utils.enums.SortingType
import com.mikeschvedov.spacex.utils.adapters.ShipsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipsFragment : Fragment() {

    private var _binding: FragmentShipsBinding? = null
    private val binding get() = _binding!!

    lateinit var shipsViewModel: ShipsViewModel

    lateinit var adapter: ShipsAdapter

    lateinit var currentSortingState: SortingType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /* ViewModel */
        shipsViewModel =
            ViewModelProvider(this)[ShipsViewModel::class.java]

        /* Binding */
        _binding = FragmentShipsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Adapter */
        adapter = shipsViewModel.getShipsAdapter()
        binding.shipsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.shipsRecyclerview.adapter = adapter
        /* Setup Menu */
        setupMenu()

        /* Get data from database to be displayed */
        decideFirstTimeFetching()

        /* Display the data on the UI */
        shipsViewModel.populateUI()

        /* Setting Observers */
        observers()

        return root
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_ships, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.maxWidth = Integer.MAX_VALUE
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        searchDB(query)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.byTitle -> {
                        currentSortingState = SortingType.ByTitle
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun decideFirstTimeFetching() {
        // If its the first running the app -> update the database,
        // Also save in shared pref a Boolean that keeps track of "isFirstRun"
        // After the first time, it will not update the DB (at least not from here), only fetch data from it.
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirstRun = sharedPref?.getBoolean("isFirstRun", true) // the default is true
        if (isFirstRun == true) {
            // Check if there is an internet connection
            if (shipsViewModel.networkStatusChecker.hasInternetConnection()) {
                // If its the first time running the app, update the DB
                shipsViewModel.updateDB()
                // Set shared pref that it is no longer first time
                with(sharedPref.edit()) {
                    putBoolean("isFirstRun", false)
                    apply()
                }
            } else {
                Toast.makeText(requireContext(), "No Internet Connection, Could Not Get Data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observers() {
        // Observing the list of ships LiveData
        shipsViewModel.listOfShips.observe(viewLifecycleOwner) { listOfShips: List<Ship> ->
            adapter.setNewData(listOfShips)
        }
        // Observing the clicked ship LiveData
        shipsViewModel.selectedShip.observe(viewLifecycleOwner) { clickedShip: SingleEvent<Ship> ->
            // The single even will return the content(ship) only if it was never handled before,
            // if it was handled before,  it will return null.
            clickedShip.getContentIfNotHandled()
                ?.let { ship ->  // if the method is not returning null, get use the content.
                    startDetailsFragment(ship)
                }
        }

    }

    private fun startDetailsFragment(ship: Ship?) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable(getString(R.string.shipExtra), ship)
        findNavController().navigate(R.id.action_nav_ships_to_nav_ships_details, bundle)
    }

    private fun searchDB(query: String) {
        shipsViewModel.searchInDB(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}