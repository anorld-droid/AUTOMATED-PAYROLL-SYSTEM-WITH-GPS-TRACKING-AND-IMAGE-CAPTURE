package com.example.workethic.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.workethic.Pojo.Employee
import com.example.workethic.Pojo.InternalStorage
import com.example.workethic.Pojo.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class MainviewModel(
    private val repository: Repository
) : ViewModel() {

    var list_of_id = ArrayList<String>()

    //method to getlistofemployees
    fun getEmployeeIdList() {

        val response = repository.getListOfEmployees()
        response.enqueue(object : Callback<Employee> {
            override fun onResponse(call: Call<Employee>, response: Response<Employee>) {

                if (response.isSuccessful) {
                    response.body().let {
                        for (id in it?.results!!) {
                            list_of_id.add(id.id)
                        }

                        //   Log.d("POST", "onResponse:${list_of_id} ")
                    }
                } else {
                    Log.d("MYERROR", "onResponse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Employee>, t: Throwable) {
                Log.d("NORES", "onFailure:${t.message.toString()} ")
            }
        })
    }


    //save image to internal storage
    fun saveToInternalStorage(name: String, bitmap: Bitmap, context: Context): Boolean {
        return try {
            context.openFileOutput("$name.jpg", AppCompatActivity.MODE_PRIVATE).use {
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

    //load photo
    fun loadPhotoFromInternalStorage(context: Context): List<InternalStorage> {
        val files = context.filesDir.listFiles()
        return files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
            val bytes = it.readBytes()
            val btmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            InternalStorage(it.name, btmp)
        } ?: listOf()
    }

    //deletefile
    fun deleteFileFromInternalStorage(context: Context, file_name: String): Boolean {
        return try {
            context.deleteFile(file_name)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun listOfImages(context: Context): List<File> {
        val files = context.filesDir.listFiles()
        return files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
            it
        } ?: listOf()
    }

    fun updateUserDetails(
        token: String,
        id: String,
        body: HashMap<String, RequestBody>,
        image: MultipartBody.Part?
    ) {

        val response = repository.updateUserDetails(token, id, body, image)
        response.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful) {
                    Log.d("PATCHI", "onResponse: ${response.body()}")
                } else {
                    Log.d("PATCHER", "onResponse: ${response.body()} ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("PATCHER", "onFailure: ${t.message.toString()}")

            }

        })
    }
    fun updateStatusDetails(
        token: String,
        id: String,
        body: HashMap<String, RequestBody>,
    ) {

        val response = repository.updateStatusDetails(token, id, body)
        response.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful) {
                    Log.d("PATCHI", "onResponse: ${response.body()}")
                } else {
                    Log.d("PATCHER", "onResponse: ${response.body()} ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("PATCHER", "onFailure: ${t.message.toString()}")

            }

        })
    }



}