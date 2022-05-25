package com.example.workethic.Retrofit

import com.example.workethic.Pojo.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApi {


    @Multipart
    @PUT("employees/")
    fun postToServer(
        @Header("Authorization") token: String,
        @PartMap body: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Call<Result>

}