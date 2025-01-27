package com.example.witt.presentation.ui.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.witt.R
import com.example.witt.data.model.remote.detail_plan.search.PlaceModel
import com.example.witt.data.model.remote.detail_plan.search.toAddPlaceRequest
import com.example.witt.databinding.FragmentAddPlaceBinding
import com.example.witt.presentation.base.BaseFragment
import com.example.witt.presentation.ui.UiEvent
import com.example.witt.presentation.ui.plan.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class AddPlaceFragment : BaseFragment<FragmentAddPlaceBinding>(R.layout.fragment_add_place) {

    private val args: AddPlaceFragmentArgs by navArgs()
    private val viewModel: AddPlaceViewModel by viewModels()
    private val planViewModel: PlanViewModel by activityViewModels()
    private val mapView by lazy { MapView(requireActivity()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initMap()
        initView()
    }

    private fun observeData() {
        planViewModel.planState.observe(viewLifecycleOwner) {
            viewModel.setInfo(it.TripId, args.day)
        }

        viewModel.addPlaceEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                when (it) {
                    is UiEvent.Success -> {
                        removeMap()
                        val direction = AddPlaceFragmentDirections
                            .actionAddPlaceFragmentToDrawUpPlanFragment()
                        findNavController().navigate(direction)
                    }
                    is UiEvent.Failure -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initView() {
        binding.addPlaceButton.setOnClickListener {
            viewModel.addPlace(args.place.toAddPlaceRequest())
        }
    }

    private fun initMap() {
        val place: PlaceModel = args.place
        val xPosition = place.x.toDouble()
        val yPosition = place.y.toDouble()

        // initMapView
        binding.mapView.addView(mapView)
        with(mapView) {
            setMapCenterPoint(MapPoint.mapPointWithGeoCoord(yPosition, xPosition), true)
            setZoomLevel(1, true)
        }

        // init Marker
        val marker = MapPOIItem()
        with(marker) {
            itemName = place.place_name
            tag = 0
            mapPoint = MapPoint.mapPointWithGeoCoord(yPosition, xPosition)
            markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
            selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때 RedPin 마커 모양.
        }
        mapView.addPOIItem(marker)
    }

    private fun removeMap() {
        binding.mapView.removeView(mapView)
    }
}
