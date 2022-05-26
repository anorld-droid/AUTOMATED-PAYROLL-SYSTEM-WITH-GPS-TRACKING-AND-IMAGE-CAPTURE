package com.example.workethic.Retrofit

import com.example.workethic.Pojo.Employee
import com.example.workethic.Pojo.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApi {


    @Multipart
    @PATCH("employee/{id}/")
    fun updateUserDetails(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @PartMap body: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Call<Result>

    @GET("employees")
    fun getListofEmployees():Call<Employee>
}