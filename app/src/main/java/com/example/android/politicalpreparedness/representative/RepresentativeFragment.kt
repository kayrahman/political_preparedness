package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.source.remote.RemoteElectionDatasource
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale

class DetailFragment : BaseFragment() {

    companion object {
        //TODO: Add Constant for Location request
        private val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }

    //TODO: Declare ViewModel
    override val _viewModel by viewModels<RepresentativeViewModel>{
        RepresentativeViewModelFactory(RemoteElectionDatasource(),activity?.application!!)
    }
    private lateinit var binding:FragmentRepresentativeBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater,container,false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        setUpAdapter()

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            getLocation()
        }

        binding.buttonSearch.setOnClickListener {
            //fetch representatives from remote and populate rv
            hideKeyboard()
            _viewModel.fetchRepresentativesFromRemote()
        }

    }

    private fun setUpAdapter() {
        val adapter = RepresentativeListAdapter()
        binding.rvMyRepresentatives.adapter = adapter
        _viewModel.representatives.observe(viewLifecycleOwner, Observer {
           Timber.d("size_reps${it.size}")
            adapter.submitList(it)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            getUserLocationIfDeviceLocationOn(false)
        }
    }


    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
     return ActivityCompat.checkSelfPermission(requireActivity(),
             Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        // Get location from LocationServices
        if(checkLocationPermissions()){
            //make a location request and check if the location is enabled
            getUserLocationIfDeviceLocationOn()
        }
        //The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun getUserLocationIfDeviceLocationOn(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    startIntentSenderForResult(exception.resolution.intentSender,REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                        binding.fmRepresentativeRoot,
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    //checkDeviceLocationSettingsAndStartGeofence()
                    getLastknownLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                //get user location
                getLastknownLocation()

            }else{
                Log.d("isLocationEnabled","failure")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastknownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if(it != null ){
                try {
                    val add = geoCodeLocation(it)
                    _viewModel.updateAddress(add)
                }catch (e:Exception){
                    _viewModel.showSnackBar.value = resources.getString(R.string.loc_error)
                    Timber.d("location error${e.toString()}}")
                }

              }else{
                _viewModel.showSnackBar.value = resources.getString(R.string.loc_not_found)
                Timber.d("location not found}")
            }
        }
    }

}