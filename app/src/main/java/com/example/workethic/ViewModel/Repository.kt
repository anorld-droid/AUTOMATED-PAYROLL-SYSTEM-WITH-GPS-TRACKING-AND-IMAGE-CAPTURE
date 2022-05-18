package com.example.workethic.ViewModel

import com.example.workethic.Pojo.Employee
import com.example.workethic.Retrofit.RetrofitApi
import retrofit2.Call

class Repository {
    suspend fun postToServer(employeeData:Employee):Call<Employee>{
        return  RetrofitApi.postToServer(employeeData)
    }
}