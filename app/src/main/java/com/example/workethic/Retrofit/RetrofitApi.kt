package com.example.workethic.Retrofit

import com.example.workethic.Pojo.Employee
import com.example.workethic.Pojo.Result
import com.example.workethic.util.AUNTHETIFICATION
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApi {
    @POST("employees")
    suspend fun postToServer(
        @Header("Authorization") token: String,
        @Body employeeData: Employee
    ): Response<Employee>

}