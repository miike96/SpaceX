package com.mikeschvedov.spacex.ui.ships.detailscreen

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
import com.mikeschvedov.spacex.R
import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.database.entities.Ship
import com.mikeschvedov.spacex.databinding.FragmentDetailsShipsBinding
import com.mikeschvedov.spacex.utils.helper_classes.SingleEvent
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShipsDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsShipsBinding? = null
    private val binding get() = _binding!!

    lateinit var shipDetailsViewModel: ShipsDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        shipDetailsViewModel =
            ViewModelProvider(this)[ShipsDetailsViewModel::class.java]

        _binding = FragmentDetailsShipsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        observers()

        settingPassedArguments()

        return root
    }

    private fun observers() {

        // We setup the adapter and populate it with the Launches
        val adapter = shipDetailsViewModel.getAdapter()
        binding.launchesOfShipRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.launchesOfShipRecyclerview.adapter = adapter

        shipDetailsViewModel.listOfLaunches.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Observing the clicked launch LiveData
        shipDetailsViewModel.selectedLaunch.observe(viewLifecycleOwner) { clickedLaunch: SingleEvent<Launch> ->
            // The single even will return the content(launch) only if it was never handled before,
            // if it was handled before,  it will return null.
            clickedLaunch.getContentIfNotHandled()
                ?.let { launch ->  // if the method is not returning null, get use the content.
                    startDetailsFragment(launch)
                }
        }
    }

    private fun startDetailsFragment(launch: Launch?) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable(getString(R.string.extraLaunch), launch)
        findNavController().navigate(R.id.action_nav_ships_details_to_nav_launches_details, bundle)
    }

    private fun settingPassedArguments() {

        val selectedShip = arguments?.getParcelable<Ship>(getString(R.string.shipExtra))

        /* ------  Setting the Name and Other Views ------ */
        binding.shipNameTextview.text = selectedShip?.name.toString()
        binding.shipTypeTextview.text = selectedShip?.type
        if(selectedShip?.year_built == 0){
            binding.shipYearTextview.text = getString(com.mikeschvedov.spacex.R.string.unavailable)
        }else{
            binding.shipYearTextview.text = selectedShip?.year_built.toString()
        }

        /* ------ Setting the Image View ------ */
        val image = selectedShip?.image
        if (image != null) {
            Picasso.get().load(Uri.parse(image))
                .placeholder(R.drawable.loading)
                .error(R.drawable.ship)
                .into(binding.shipPicImageview)
        }

        /* ------ Setting the Launches RecyclerView ------ */
        // We get the Launch Ids
        var launchesIds: List<String> = listOf()
        selectedShip?.launches?.let {
            launchesIds = selectedShip.launches
        }

        // We get the Launch Objects using their Ids
        shipDetailsViewModel.getLaunchesByIds(launchesIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}