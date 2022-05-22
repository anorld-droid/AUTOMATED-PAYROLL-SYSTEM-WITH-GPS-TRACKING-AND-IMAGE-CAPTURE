package com.example.workethic.Retrofit

import com.example.workethic.Pojo.Employee
import com.example.workethic.Pojo.Result
import com.example.workethic.util.AUNTHETIFICATION
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {
    @Multipart
    @POST("employees")
     fun postToServer(
        @Header("Authorization") token: String,
        @Part("owner") owner:String,
        @Part("id") id:String,
        @Part("first_name") first_name:String,
        @Part("last_name") last_name:String,
        @Part image:MultipartBody.Part?,
        @Part("job_name") job_name:String,
        @Part("hire_date") hire_date:String,
        @Part("status") status:Int,
        @Part("department.name") department:String,
        @Part("salary.basic_salary") basic_salary:Int,
        @Part("salary.commission") commission:Int,
        @Part("location.one_hour") one_hour:String,
        @Part("location.two_hours") two_hours:String,
        @Part("location.three_hours") three_hours:String

        ): Call<Employee>

}