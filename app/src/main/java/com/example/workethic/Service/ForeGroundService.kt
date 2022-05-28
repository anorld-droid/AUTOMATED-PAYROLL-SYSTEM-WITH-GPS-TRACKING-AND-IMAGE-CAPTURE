package com.example.workethic.Service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.workethic.Pojo.HasPermissions
import com.example.workethic.UI.Activity.EmployeeActivity
import com.example.workethic.R
import com.example.workethic.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import java.util.*
import java.util.jar.Manifest


class ForeGroundService : LifecycleService() {

    companion object {
        //check if logged in
        val isLoggedIn = MutableLiveData<Boolean>()
    }

    override fun onCreate() {
        super.onCreate()
        initialValue()
    }

    private fun initialValue() {
        isLoggedIn.postValue(false)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_SERVICE -> {
                    startForegroundService()
                }
                STOP_SERVICE -> {
                    stopSelf()
                }
                else -> {}//do nothing
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //create fun to start foreground service
    private fun startForegroundService() {

        isLoggedIn.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager //needed whenever we want to show notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel(notificationManager)
        }

        //create the notification
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_announcement_24)
            .setContentTitle("Work Ethic")
            .setContentText("Logged In")
            .setContentIntent(getPendingIntent())




        startForeground(NOTIFICATION_ID, notificationBuilder.build())


    }

    //create pending intent
    private fun getPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, EmployeeActivity::class.java).also {
            it.action = SHOW_EMPLOYEE_ACTIVITY
        },
        FLAG_UPDATE_CURRENT
    )

    //create notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}