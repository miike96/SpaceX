package com.example.spacex.ui.ships.detailscreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacex.R
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.databinding.FragmentDetailsShipsBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipsDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsShipsBinding? = null
    private val binding get() = _binding!!

    private lateinit var shipDetailsViewModel: ShipsDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsShipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shipDetailsViewModel = ViewModelProvider(this).get(ShipsDetailsViewModel::class.java)

        setupObservers()
        settingPassedArguments()
    }

    private fun setupObservers() {
        val adapter = shipDetailsViewModel.getAdapter()
        binding.launchesOfShipRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.launchesOfShipRecyclerview.adapter = adapter

        shipDetailsViewModel.listOfLaunches.observe(viewLifecycleOwner) { listOfLaunches ->
            adapter.submitList(listOfLaunches)
        }

        shipDetailsViewModel.selectedLaunch.observe(viewLifecycleOwner) { clickedLaunch ->
            clickedLaunch.getContentIfNotHandled()?.let { launch ->
                startDetailsFragment(launch)
            }
        }
    }

    private fun startDetailsFragment(launch: Launch?) {
        val bundle = bundleOf(getString(R.string.extraLaunch) to launch)
        findNavController().navigate(R.id.action_nav_ships_details_to_nav_launches_details, bundle)
    }

    private fun settingPassedArguments() {
        val selectedShip = arguments?.getParcelable<Ship>(getString(R.string.shipExtra))

        binding.shipNameTextview.text = selectedShip?.name
        binding.shipTypeTextview.text = selectedShip?.type
        binding.shipYearTextview.text = if (selectedShip?.year_built != 0) selectedShip?.year_built.toString() else getString(R.string.unavailable)

        val image = selectedShip?.image
        if (image != null) {
            Picasso.get().load(Uri.parse(image))
                .placeholder(R.drawable.loading)
                .error(R.drawable.ship)
                .into(binding.shipPicImageview)
        }

        var launchesIds: List<String> = emptyList()
        selectedShip?.launches?.let {
            launchesIds = selectedShip.launches
        }

        shipDetailsViewModel.getLaunchesByIds(launchesIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
