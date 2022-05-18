package com.example.workethic.UI.Fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.workethic.Pojo.GeofenceBroadCastReceiver
import com.example.workethic.Pojo.HasPermissions
import com.example.workethic.R
import com.example.workethic.databinding.FragmentHomeBinding
import com.example.workethic.util.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.vmadalin.easypermissions.EasyPermissions


class FragmentHome : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    var _binding: FragmentHomeBinding? = null
    val binding: FragmentHomeBinding
        get() = _binding!!

    //drawing current location on map
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //geofencing purposes
    val geofenceIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadCastReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    //geofencing client
    private lateinit var geoClient: GeofencingClient

    //geofence
    private lateinit var geofence: Geofence


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        //initialize geofencing client
        geoClient = GeofencingClient(requireContext())

        //set up geofence
        geofence = Geofence.Builder()
            .setRequestId("Enter")
            .setCircularRegion(LATITUDE, LONGITUDE, RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(LOCATION_UPDATE_INTERVAL.toInt())
            .build()
//add geofence
        addGeofence()


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }






    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        setUpMap()
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        if (HasPermissions.hasPermissions(requireContext())) {
            mMap.isMyLocationEnabled = true
            var pos = LatLng(LATITUDE, LONGITUDE)
            placeMarkerAndGeofence(pos)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,18f))
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//                if (it != null) {
//                    lastLocation = it
//                    var pos = LatLng(lastLocation.latitude, lastLocation.longitude)
//                    placeMarkerAndGeofence(pos)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
//
//                }
            }

        }
    }

    private fun placeMarkerAndGeofence(pos: LatLng) {
        val markerOptions = MarkerOptions().position(pos)
        markerOptions.title("$pos")
        val cirleOptions = CircleOptions().center(pos)
            .radius(RADIUS.toDouble())
            .strokeColor(android.graphics.Color.argb(50,70,70,70))
            .fillColor(android.graphics.Color.argb(70,150,150,150))

        mMap.addMarker(markerOptions)
        mMap.addCircle(cirleOptions)

    }

    override fun onMarkerClick(p0: Marker) = false
    //geofence request
    private fun geoFencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }

    //function to add geofence
    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        if (HasPermissions.hasPermissions(requireContext())) {

            geoClient.addGeofences(geoFencingRequest(), geofenceIntent).run {
                addOnSuccessListener {
                    Log.d("Gvincent", "addGeofence: successeful")

                }
                addOnFailureListener {
                    Log.d("Fvincent", "addGeofence: ${it.message}")
                }


            }
        }
    }
    //create a notification
    companion object {
        fun showNotification(context: Context) {
            //create the notification
            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_GEO_ID)
                .setSmallIcon(R.drawable.ic_baseline_announcement_24)
                .setContentTitle("Work Ethic")
                .setContentText("Please Log out, Not within work area")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_GEO_ID,
                    NOTIFICATION_GEO_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(GEO_ID,notificationBuilder.build())

        }
    }

}