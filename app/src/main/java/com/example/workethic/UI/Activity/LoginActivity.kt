package com.example.workethic.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.Location.distanceBetween
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workethic.Pojo.*
import com.example.workethic.Retrofit.ServiceBuilder
import com.example.workethic.Service.ForeGroundService
import com.example.workethic.ViewModel.MainviewModel
import com.example.workethic.ViewModel.RecViewModelFactory
import com.example.workethic.ViewModel.Repository
import com.example.workethic.databinding.LoginActivityBinding
import com.example.workethic.util.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.MapView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.Result
import kotlin.collections.HashMap
import kotlin.math.log

class LoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: LoginActivityBinding

    //for getting current location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //view model
    private val _mViewModel: MainviewModel by lazy {
        ViewModelProvider(this, RecViewModelFactory(Repository(ServiceBuilder.api))).get(
            MainviewModel::class.java
        )
    }

    //Image Request body
    private lateinit var image_body: RequestBody


    //map
    val string_map: HashMap<String, RequestBody> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()
        //preload ids
        _mViewModel.getEmployeeIdList()


        navigateToEmployeeActivity(intent)//incase activity was destroyed


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        //take photo and save it
        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if (it != null) {
                val succcess =
                    _mViewModel.saveToInternalStorage(UUID.randomUUID().toString(), it, this)
                if (succcess) {
                    loadToImageView()
                } else {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(this, "Image is required to proceed", Toast.LENGTH_SHORT).show()

            }

        }


        //put status values

        string_map.put("status", mapToRequestBody("1"))



        binding.ivImage.setOnClickListener {
            if (HasPermissions.hasPermissions(this)) {
                takePhoto.launch()
            } else {
                requestPermissions()
            }

        }
        binding.loginButton.setOnClickListener {
            // requestPermissions()

            validateUserInputAndUpdate()


            Log.d("SID", "onCreate: successful ${checkUserLocation()}")


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

    //send commands to service class

    private fun sendCommandsToService(action: String) {
        Intent(this, ForeGroundService::class.java).also {
            it.action = action
            this.startService(it)

        }
    }

    //check location of user
    @SuppressLint("MissingPermission")
    private fun checkUserLocation(): Float {
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


    //load to imaage view
    private fun loadToImageView() {
        val photos = _mViewModel.loadPhotoFromInternalStorage(this)
        val image = photos[0].image
        binding.ivImage.setImageBitmap(image)

    }

    //get name of image
    private fun getImageName(): String {
        return _mViewModel.loadPhotoFromInternalStorage(this)[0].name

    }

    private fun mapToRequestBody(value: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, value)
    }

    //validate user inoput
    private fun validateUserInputAndUpdate() {
        val files = _mViewModel.listOfImages(this)

        if (binding.idEditTextInput.text == null || files.isEmpty()) {
            Toast.makeText(this, "Field or Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user_id = binding.idEditTextInput.text.toString()
        if (!_mViewModel.list_of_id.contains(user_id)) {
            Toast.makeText(this, "Please check your ID and try again", Toast.LENGTH_SHORT).show()
            return
        }
        GetId_statusState.id = user_id
        image_body =
            files[0].let { it1 ->
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    it1
                )
            }

        sendCommandsToService(START_SERVICE)
        val intent = Intent(this, EmployeeActivity::class.java)
        startActivity(intent)

        _mViewModel.updateUserDetails(
            AUNTHETIFICATION,
            GetId_statusState.id,
            string_map,
            MultipartBody.Part.createFormData("image", files[0].name, image_body)

        )

        _mViewModel.deleteFileFromInternalStorage(this, getImageName())
    }


}

