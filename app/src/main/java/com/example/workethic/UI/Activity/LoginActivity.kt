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
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workethic.Pojo.*
import com.example.workethic.Retrofit.ServiceBuilder
import com.example.workethic.Service.ForeGroundService
import com.example.workethic.databinding.LoginActivityBinding
import com.example.workethic.util.*
import com.google.android.gms.location.*
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
import kotlin.collections.HashMap
import kotlin.math.log

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
        //take photo
        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val succcess = saveToInternalStorage("Mypic", it!!)
            if (succcess) {
                //loadToImageView()
                Toast.makeText(this, "success ${filesDir}", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()

            }
        }
//map for values to upload
        val string_map: HashMap<String, RequestBody> = HashMap()
        val int_map: HashMap<String, Int> = HashMap()


        //put values
        string_map.put("owner", mapToRequestBody("Admin"))
        string_map.put("id", mapToRequestBody("MYIDadfjheuhiereu"))
        string_map.put("first_name", mapToRequestBody("Kirui"))
        string_map.put("last_name", mapToRequestBody("PAtro"))
        string_map.put("hire_date", mapToRequestBody("2022-05-08"))
        string_map.put("job_name", mapToRequestBody("IT"))
        string_map.put("department.name", mapToRequestBody("IT support"))
        string_map.put("location.one_hour", mapToRequestBody("1332423l:236846N"))
        string_map.put("location.two_hours", mapToRequestBody("1332423l:236846N"))
        string_map.put("location.three_hours", mapToRequestBody("1332423l:236846N"))
        string_map.put("status", mapToRequestBody("1"))
        string_map.put("salary.basic_salary", mapToRequestBody("100000"))
        string_map["salary.commission"] = mapToRequestBody("10000")





        binding.ivImage.setOnClickListener {
            takePhoto.launch()

        }
        binding.loginButton.setOnClickListener {

            // oneTimeWork()
            if (checkUserLocation() <= RADIUS) {

                val file: Array<out File>? = filesDir.listFiles()
                if (file != null) {
                    if (file.isEmpty()) {
                        Toast.makeText(this, "image required", Toast.LENGTH_SHORT).show()


                    } else {
                        Toast.makeText(
                            this,
                            "${filesDir.listFiles()?.get(0)?.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        val image_body =
                            file.get(1).let { it1 ->
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    it1
                                )
                            }
                        // upload
                        val image = image_body?.let { it1 ->
                            MultipartBody.Part.createFormData(
                                "image", file.get(0).name,
                                it1
                            )
                        }

                        ServiceBuilder.api.postToServer(
                            AUNTHETIFICATION,
                            string_map,
                            image,
                        ).enqueue(object : Callback<Result> {
                            override fun onResponse(
                                call: Call<Result>,
                                response: Response<Result>
                            ) {
                                if (response.isSuccessful && response.body() != null) {
                                    Log.d("MYRES", "onResponse: ${response.body()}")
                                } else {
                                    Log.d(
                                        "MyError",
                                        "onResponse: not succesful ${response.errorBody()?.charStream()?.readText()} ${response.code()}"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<Result>, t: Throwable) {
                                Log.d("MYERR", "onFailure: ${t.message.toString()}")
                            }
                        })
                    }
                }
//                sendCommandsToService(START_SERVICE)
//                val intent = Intent(this, EmployeeActivity::class.java)
//                startActivity(intent)
//                Log.d("SID", "onCreate: successful ${checkUserLocation()}")

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


    // work manager function for initial login
    private fun oneTimeWork() {
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DefineOneTimeWork::class.java)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("initialPost", ExistingWorkPolicy.KEEP, oneTimeWorkRequest)
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

    //save image to internal storage
    private fun saveToInternalStorage(name: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$name.jpg", MODE_PRIVATE).use {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)) {
                    throw IOException("Could not save")
                }

            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }

    //return file
    @SuppressLint("FileEndsWithExt")
    private fun getImageFiles(): List<File>? {
        val file = filesDir.listFiles()
        return file?.filter {
            it.canRead() &&
                    it.isFile && it.endsWith(".jpg")
        }?.map {
            val bytes = it.readBytes()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            it
        } ?: listOf()


    }

    //load to imaage view
    private fun loadToImageView() {
        val photos = getImageFiles()
        val photo = photos?.get(0)


    }

    private fun mapToRequestBody(value: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, value)
    }

}

