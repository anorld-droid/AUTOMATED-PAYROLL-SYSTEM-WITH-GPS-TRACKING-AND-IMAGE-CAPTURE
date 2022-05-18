package com.example.workethic.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.Location.distanceBetween
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workethic.Pojo.DefineOneTimeWork
import com.example.workethic.Pojo.HasPermissions
import com.example.workethic.Service.ForeGroundService
import com.example.workethic.databinding.LoginActivityBinding
import com.example.workethic.util.*
import com.google.android.gms.location.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class LoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: LoginActivityBinding

    //for getting current location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()
        navigateToEmployeeActivity(intent)//incase activity was destroyed

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


        binding.ivImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(intent, CAMERA_INTENT_CODE)

        }
        binding.loginButton.setOnClickListener {
            oneTimeWork()
            if (checkUserLocation() <= RADIUS) {
                sendCommandsToService(START_SERVICE)
                val intent = Intent(this, EmployeeActivity::class.java)
                startActivity(intent)
                Log.d("SID", "onCreate: successful ${checkUserLocation()}")

            } else {
                Toast.makeText(this, "Must be within work place", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToEmployeeActivity(intent)

    }

    //navigate to our EmployeeActivity
    private fun navigateToEmployeeActivity(intent: Intent?) {
        if (intent?.action == SHOW_EMPLOYEE_ACTIVITY) {
            startActivity(intent)
        }
    }


    //Request Permissions
    private fun requestPermissions() {
        if (HasPermissions.hasPermissions(this)) {
            return
        }


        if (Build.VERSION.SDK_INT < Q) {
            EasyPermissions.requestPermissions(
                this,
                "This app Cannot work Correctly without this permission",
                REQUEST_CODE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app Cannot work Correctly without this permission",
                REQUEST_CODE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

            )
        }


    }

    //send commands to service class

    private fun sendCommandsToService(action: String) {
        Intent(this, ForeGroundService::class.java).also {
            it.action = action
            this.startService(it)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()

        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_INTENT_CODE) {
                binding.ivImage.setImageBitmap(data!!.extras!!.get("data") as Bitmap)
            }
        }
    }


   // work manager function for initial login
    private fun oneTimeWork() {
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DefineOneTimeWork::class.java)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork("initialPost",ExistingWorkPolicy.KEEP,oneTimeWorkRequest)
    }


    //check location of user
    @SuppressLint("MissingPermission")
    private fun checkUserLocation(): Float{
        val results = FloatArray(1)
        if (HasPermissions.hasPermissions(this)) {

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {

                    lastLocation = it
                    distanceBetween(
                        LATITUDE,
                        LONGITUDE,
                        lastLocation.latitude,
                        lastLocation.longitude,
                        results
                    )
                }
            }


        }
        return results[0]

    }

}

