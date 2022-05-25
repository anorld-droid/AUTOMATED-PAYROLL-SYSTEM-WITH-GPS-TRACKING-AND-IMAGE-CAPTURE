package com.example.workethic.Retrofit

import com.example.workethic.util.BASEURL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    val api: RetrofitApi by lazy {
        Retrofit.Builder().
        baseUrl(BASEURL).
        addConverterFactory(GsonConverterFactory.create()).
        build().
        create(RetrofitApi::class.java)
    }
}