//package com.example.workethic.ViewModel
//
//import com.example.workethic.Pojo.Employee
//import com.example.workethic.Pojo.Result
//import com.example.workethic.Retrofit.RetrofitApi
//import retrofit2.Call
//import retrofit2.Response
//
//class Repository(private val retrofitApi: RetrofitApi) {
//    suspend fun postToServer(employeeData:Result):Response<Result>{
//        return  retrofitApi.postToServer(employeeData)
//    }
//}