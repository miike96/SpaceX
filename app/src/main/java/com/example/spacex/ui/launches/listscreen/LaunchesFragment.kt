package com.example.spacex.ui.launches.listscreen

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacex.R
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.databinding.FragmentLaunchesBinding
import com.example.spacex.utils.adapters.LaunchesAdapter
import com.example.spacex.utils.enums.SortingType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LaunchesFragment : Fragment() {

    private var _binding: FragmentLaunchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaunchesViewModel by viewModels()

    private lateinit var adapter: LaunchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaunchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        adapter = LaunchesAdapter { clickedLaunch ->
            viewModel.selectLaunch(clickedLaunch)
        }
        binding.launchesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.launchesRecyclerview.adapter = adapter

        binding.goToTopChip.setOnClickListener {
            binding.launchesRecyclerview.scrollToPosition(0)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_launches, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.searchInDB(query, SortingType.ByTitle)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.byTitle -> {
                viewModel.searchInDB("", SortingType.ByTitle)
                binding.sortingChip.text = getString(R.string.sortedbytitle)
                true
            }
            R.id.byDateASC -> {
                viewModel.searchInDB("", SortingType.ByDateASC)
                binding.sortingChip.text = getString(R.string.sortedbyAsc)
                true
            }
            R.id.byDateDESC -> {
                viewModel.searchInDB("", SortingType.ByDateDESC)
                binding.sortingChip.text = getString(R.string.sortedbydesc)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupObservers() {
        viewModel.listOfLaunches.observe(viewLifecycleOwner) { listOfLaunches ->
            adapter.setNewData(listOfLaunches)
        }

        viewModel.selectedLaunch.observe(viewLifecycleOwner) { clickedLaunch ->
            clickedLaunch.getContentIfNotHandled()?.let { launch ->
                startDetailsFragment(launch)
            }
        }
    }

    private fun startDetailsFragment(launch: Launch?) {
        val bundle = Bundle().apply {
            putParcelable(getString(R.string.selectedLaunch), launch)
        }
        findNavController().navigate(R.id.action_nav_launches_to_nav_launches_details, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
