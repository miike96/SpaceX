package com.example.spacex.ui.launches.detailscreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacex.R
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.data.database.entities.Ship
import com.example.spacex.databinding.FragmentDetailsLaunchesBinding
import com.example.spacex.utils.fromUnixToFormatted
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsLaunchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaunchDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsLaunchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupPassedArguments()
    }

    private fun setupObservers() {
        val adapter = viewModel.getAdapter()
        binding.shipsOfLaunchRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.shipsOfLaunchRecyclerview.adapter = adapter

        viewModel.listOfShips.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.selectedShip.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { ship ->
                startDetailsFragment(ship)
            }
        }
    }

    private fun startDetailsFragment(ship: Ship?) {
        val bundle = Bundle().apply {
            putParcelable(getString(R.string.shipExtra), ship)
        }
        findNavController().navigate(R.id.action_nav_launches_details_to_nav_ships_details, bundle)
    }

    private fun setupPassedArguments() {
        val selectedLaunch = arguments?.getParcelable<Launch>(getString(R.string.extraLaunch))

        selectedLaunch?.apply {
            binding.launchNameTextview.text = name
            binding.launchSuccessTextview.text = if (success == true) getString(R.string.yes) else getString(R.string.no)
            binding.launchDateTextview.text = date_unix?.fromUnixToFormatted()

            val image = links?.patch?.large
            image?.let {
                Picasso.get().load(Uri.parse(it))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ship)
                    .into(binding.launchPicImageview)
            }

            ships?.let { shipIds ->
                viewModel.getShipsByIds(shipIds)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
