package com.example.spacex.ui.launches.detailscreen

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
import com.example.spacex.databinding.FragmentDetailsLaunchesBinding
import com.example.spacex.utils.helper_classes.SingleEvent
import com.example.spacex.utils.fromUnixToFormatted
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsLaunchesBinding? = null
    private val binding get() = _binding!!

    lateinit var launchDetailsViewModel: LaunchDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launchDetailsViewModel =
            ViewModelProvider(this)[LaunchDetailsViewModel::class.java]

        _binding = FragmentDetailsLaunchesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        observers()

        settingPassedArguments()

        return root
    }

    private fun observers() {
        // We setup the adapter and populate it with the Ships
        val adapter = launchDetailsViewModel.getAdapter()
        binding.shipsOfLaunchRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.shipsOfLaunchRecyclerview.adapter = adapter

        launchDetailsViewModel.listOfShips.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Observing the clicked ship LiveData
        launchDetailsViewModel.selectedShip.observe(viewLifecycleOwner) { clickedShip: SingleEvent<Ship> ->
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
        findNavController().navigate(R.id.action_nav_launches_details_to_nav_ships_details, bundle)
    }


    private fun settingPassedArguments() {
        val selectedLaunch = arguments?.getParcelable<Launch>(getString(R.string.extraLaunch))

        /* ------  Setting the Name and Other Views ------ */
        binding.launchNameTextview.text = selectedLaunch?.name.toString()
        if (selectedLaunch?.success == true){
            binding.launchSuccessTextview.text = getString(R.string.yes)
        }else{
            binding.launchSuccessTextview.text = getString(R.string.no)
        }
        binding.launchDateTextview.text = selectedLaunch?.date_unix?.fromUnixToFormatted()

        /* ------ Setting the Image View ------ */
        val image = selectedLaunch?.links?.patch?.large
        if (image != null) {
            Picasso.get().load(Uri.parse(image))
                .placeholder(R.drawable.loading)
                .error(R.drawable.ship)
                .into(binding.launchPicImageview)
        }

        /* ------ Setting the Launches RecyclerView ------ */
        // We get the Launch Ids
        var shipsIds: List<String> = listOf()
        selectedLaunch?.ships?.let {
            shipsIds = selectedLaunch.ships
        }

        // We get the Ship Objects using their Ids
        launchDetailsViewModel.getShipsByIds(shipsIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}