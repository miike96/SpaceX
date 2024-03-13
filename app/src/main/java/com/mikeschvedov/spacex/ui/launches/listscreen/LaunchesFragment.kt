package com.mikeschvedov.spacex.ui.launches.listscreen

import android.os.Bundle
import android.view.*
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
import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.databinding.FragmentLaunchesBinding
import com.mikeschvedov.spacex.utils.helper_classes.SingleEvent
import com.mikeschvedov.spacex.utils.enums.SortingType
import com.mikeschvedov.spacex.utils.adapters.LaunchesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchesFragment : Fragment() {

    private var _binding: FragmentLaunchesBinding? = null
    private val binding get() = _binding!!

    lateinit var launchesViewModel: LaunchesViewModel

    lateinit var adapter: LaunchesAdapter

    var currentSortingType: SortingType = SortingType.ByTitle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /* ViewModel */
        launchesViewModel =
            ViewModelProvider(this).get(LaunchesViewModel::class.java)

        /* Binding */
        _binding = FragmentLaunchesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Adapter */
        adapter = launchesViewModel.getLaunchesAdapter()
        binding.launchesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.launchesRecyclerview.adapter = adapter

        /* Setup Menu */
        setupMenu()

        setupChips()

        /* Display the data on the UI */
        launchesViewModel.searchInDB("", SortingType.ByTitle)

        /* Setting Observers */
        observers()

        return root
    }

    private fun setupChips() {
        binding.sortingChip.text = getString(R.string.sortedbytitle)

        // Go to top of list when clicked
        binding.goToTopChip.setOnClickListener {
            binding.launchesRecyclerview.scrollToPosition(0)
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_launches, menu)
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
                        currentSortingType = SortingType.ByTitle
                        launchesViewModel.searchInDB("", currentSortingType)
                        binding.sortingChip.text = getString(R.string.sortedbytitle)
                        true
                    }
                    R.id.byDateASC -> {
                        currentSortingType = SortingType.ByDateASC
                        launchesViewModel.searchInDB("", currentSortingType)
                        binding.sortingChip.text = getString(R.string.sortedbyAsc)
                        true
                    }
                    R.id.byDateDESC -> {
                        currentSortingType = SortingType.ByDateDESC
                        launchesViewModel.searchInDB("", currentSortingType)
                        binding.sortingChip.text = getString(R.string.sortedbydesc)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observers() {
        // Observing the list of ships LiveData
        launchesViewModel.listOfLaunches.observe(viewLifecycleOwner) { listOfLaunches: List<Launch> ->
            adapter.setNewData(listOfLaunches)
        }
        // Observing the clicked ship LiveData
        launchesViewModel.selectedLaunch.observe(viewLifecycleOwner) { clickedLaunch: SingleEvent<Launch> ->
            // The single even will return the content(launch) only if it was never handled before,
            // if it was handled before,  it will return null.
            clickedLaunch.getContentIfNotHandled()
                ?.let { launch ->  // if the method is not returning null, get use the content.
                    startDetailsFragment(launch)
                }
        }
    }

    private fun startDetailsFragment(ship: Launch?) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("selectedLaunch", ship)
        findNavController().navigate(R.id.action_nav_launches_to_nav_launches_details, bundle)
    }

    private fun searchDB(query: String) {
        launchesViewModel.searchInDB(query, currentSortingType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}