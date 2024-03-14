package com.example.spacex.ui.ships.listscreen

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
import com.example.spacex.R
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.databinding.FragmentShipsBinding
import com.example.spacex.utils.helper_classes.SingleEvent
import com.example.spacex.utils.enums.SortingType
import com.example.spacex.utils.adapters.ShipsAdapter
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

        shipsViewModel =
            ViewModelProvider(this)[ShipsViewModel::class.java]

        _binding = FragmentShipsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = shipsViewModel.getShipsAdapter()
        binding.shipsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.shipsRecyclerview.adapter = adapter

        setupMenu()

        decideFirstTimeFetching()

        shipsViewModel.populateUI()

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
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirstRun = sharedPref?.getBoolean("isFirstRun", true)
        if (isFirstRun == true) {
            if (shipsViewModel.networkStatusChecker.hasInternetConnection()) {
                shipsViewModel.updateDB()
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
        shipsViewModel.listOfShips.observe(viewLifecycleOwner) { listOfShips: List<Ship> ->
            adapter.setNewData(listOfShips)
        }
        shipsViewModel.selectedShip.observe(viewLifecycleOwner) { clickedShip: SingleEvent<Ship> ->
            clickedShip.getContentIfNotHandled()
                ?.let { ship ->
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
