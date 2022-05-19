//package com.example.workethic.ViewModel
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.workethic.Pojo.Employee
//import com.example.workethic.Pojo.Result
//import kotlinx.coroutines.launch
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class MainviewModel(
//    private val repository: Repository
//) : ViewModel() {
//
//    var results: MutableLiveData<Call<Result>> = MutableLiveData()
//
//    //method to post
//    fun postToServer(employeeData: Result) {
//        viewModelScope.launch {
//            val response = repository.postToServer(employeeData)
//            response.enqueue(object:Callback<Result>{
//                override fun onResponse(call: Call<Result>, response: Response<Result>) {
//
//                    if(response.isSuccessful){
//                        response.body().let {
//                            Log.d("POST", "onResponse:${it} ")
//                        }
//                    }else{
//                        Log.d("MYERROR", "onResponse: ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<Result>, t: Throwable) {
//                    Log.d("NORES", "onFailure:${t.message.toString()} ")                }
//            })
//
//            results.postValue(response)
//        }
//    }
//
//}