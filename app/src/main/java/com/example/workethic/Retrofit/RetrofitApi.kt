package com.example.workethic.Retrofit

import com.example.workethic.Pojo.Employee
import com.example.workethic.util.AUNTHETIFICATION
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApi {
    @Headers(AUNTHETIFICATION)
    @POST("employees")
   suspend fun postToServer(@Body employeeData:Employee):Call<Employee>
}