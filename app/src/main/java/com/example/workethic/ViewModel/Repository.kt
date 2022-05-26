package com.example.workethic.ViewModel


import com.example.workethic.Retrofit.RetrofitApi
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository(private val retrofitApi: RetrofitApi) {
    fun updateUserDetails(
        token: String,
        id: String,
        body: HashMap<String, RequestBody>,
        image: MultipartBody.Part?
    ) = retrofitApi.updateUserDetails(token, id, body, image)

    fun getListOfEmployees() = retrofitApi.getListofEmployees()
}
