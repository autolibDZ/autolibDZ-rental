package com.sil1.autolibdz_rental.ui.view.fragment.reservation_vehicule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.kaopiz.kprogresshud.KProgressHUD
import com.sil1.autolibdz_rental.R
import com.sil1.autolibdz_rental.ui.view.activity.MyDrawerController
import com.sil1.autolibdz_rental.ui.view.fragment.stripe_card.StripeCardViewModel
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.TravelMode
import com.sil1.autolibdz_rental.data.room.RoomService
import com.sil1.autolibdz_rental.ui.viewmodel.Reservation
import com.sil1.autolibdz_rental.ui.viewmodel.Vehicule
import com.sil1.autolibdz_rental.utils.sharedPrefFile
import kotlinx.android.synthetic.main.fragment_details_vehicule.*
import kotlinx.android.synthetic.main.fragment_vehicule_reserve2.*
import kotlinx.android.synthetic.main.stripe_card_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class VehiculeReserve2Fragment : Fragment() {
    private val TAG = "_VehiculeDeverouillerFragment"
    private lateinit var viewModel: InfosReservationViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var requestingLocationUpdates = true
    var totalDistance : Long = 0
    private lateinit var mCurrentLocation : Location
    private lateinit var locationRequest : LocationRequest
    private lateinit var vehiculeLocation : LatLng
    private lateinit var vm : Vehicule
    private var myDrawerController: MyDrawerController? = null
    private var idReservation = 0
    private lateinit var hud: KProgressHUD
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myDrawerController = try {
            activity as MyDrawerController
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement MyDrawerController")
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
       // myDrawerController?.setDrawer_UnLocked()
    }

    var code=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         vm = ViewModelProvider(requireActivity()).get(Vehicule::class.java)

        var codePin= arguments?.get("codePin")
        code = codePin.toString()

        idReservation= arguments?.getInt("id")!!
        //idReservation = 164
        Log.i("idReservation_vehicule2", idReservation.toString())

        myDrawerController?.setDrawer_Locked();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getCurrentLocation()
        createLocationRequest()
        vehiculeLocation = LatLng(vm.latitute.toDouble(), vm.longitude.toDouble()) //36.7045275 + 3.1730424
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.i(TAG,"Current location : latitude : ${location.latitude} ,longitude ${location.longitude}")
                    Log.i(TAG,"Vehicule location : latitude : ${vehiculeLocation.latitude} ,longitude ${vehiculeLocation.longitude}")

                    var res = calculateDistance(vehiculeLocation.latitude,vehiculeLocation.longitude,location.latitude,location.longitude)
                    //var currentLocation : LatLng = LatLng(location.latitude,location.longitude)
                   // makeDistanceCalculationCall(vehiculeLocation,currentLocation)
                    // write the condition to enable deverouiller button
                    if(res<20){
                        deverrouillerButton1?.isEnabled = true
                    }

                }
            }
        }
        return inflater.inflate(R.layout.fragment_vehicule_reserve2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vm = ViewModelProvider(requireActivity()).get(Vehicule::class.java)
        val sharedPref = RoomService.context.getSharedPreferences(
            sharedPrefFile, Context.MODE_PRIVATE
        )

        val token = sharedPref.getString("token","defaultvalue").toString()
        val factory = InfosReservationViewModelFactory(token)
        viewModel = ViewModelProviders.of(this, factory).get(InfosReservationViewModel::class.java)

        signalerFinTrajetBtn.setOnClickListener { signalFinTrajet() }

        Glide.with(requireActivity()).load(vm.secureUrl).into(imageVehicule1)
        Log.i(TAG,"vehicule location : ${vm.longitude} ${vm.latitute}")
        codePINTextView1.text = code
        verrouillerButton1?.isEnabled = false
        deverrouillerButton1?.isEnabled = false
        deverrouillerButton1.setOnClickListener {
            verrouillerButton1?.isEnabled = true
            deverrouillerButton1?.isEnabled = false
        }
        verrouillerButton1.setOnClickListener {
            deverrouillerButton1?.isEnabled = true
            verrouillerButton1?.isEnabled = false
        }
    }
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }
    fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it!=null) {

                    Log.i(TAG,"current Location : ${it.latitude} + ${it.longitude}")
                    mCurrentLocation = it
                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10f))

                }
                else {

                    Log.i(TAG,"current Location : null")
                    //in case current location is null

                }
            }
        }
    }

    private fun checkPermission() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION)
        val permsRequestCode = 300;
        ActivityCompat.requestPermissions(requireActivity(), perms, permsRequestCode)

    }
    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        // distance in meter
        Log.i(TAG,"Distance : ${results[0]}")
        return results[0]
    }

    fun signalFinTrajet() {
        //create payment intent
        val vmRes = ViewModelProvider(requireActivity()).get(Reservation::class.java)
        viewModel.getTrajet(idReservation)
        Log.i("idreservationsignal" , idReservation.toString())
        hud = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Patientez s'il vous plait")
        hud.show()
        viewModel.trajet.observe(requireActivity(), Observer {
            if (viewModel.trajet.value != null) {
                val handler = Handler()
                handler.postDelayed(Runnable { hud.dismiss() }, 500)
                if (viewModel.trajet.value!!.dateFin != null) {
                    val bundle = bundleOf(
                        "idReservation" to idReservation,
                        "borneDepart" to vmRes.nomBorneDepart,
                        "borneArrivee" to vmRes.nomBorneDestination,
                        "kilometres" to viewModel.trajet.value!!.kmParcourue,
                        "temps" to viewModel.trajet.value!!.tempsEstime,
                        "prixAPayer" to viewModel.trajet.value!!.prixAPayer
                    )
                    findNavController().navigate(
                        R.id.action_vehiculeReserve2Fragment_to_infosTrajetFragment,
                        bundle
                    )
                }
                else {
                    val dialog = MaterialDialog(requireActivity())
                        .title(R.string.signalODB)
                        .message(R.string.signalODBDetail)
                        .positiveButton(R.string.yes) { dialog ->
                            dialog.dismiss()
                        }
                    dialog.show()
                    Log.i("tralala", "date fin NOOON atteinte")
                }

            }
        })
    }

}