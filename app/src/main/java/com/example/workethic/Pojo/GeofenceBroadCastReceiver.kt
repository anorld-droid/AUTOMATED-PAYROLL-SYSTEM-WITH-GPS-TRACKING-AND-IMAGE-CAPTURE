package com.example.workethic.Pojo

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.workethic.R
import com.example.workethic.UI.Activity.LoginActivity
import com.example.workethic.UI.Fragments.FragmentHome
import com.example.workethic.databinding.FragmentHomeBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadCastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.d("BroadCast", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition ==  Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("Dsuccess", "onReceive: staying ")


        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            FragmentHome.showNotification(context!!.applicationContext)
            Log.d("Dfail", "onReceive: not in")


        }
    }
}