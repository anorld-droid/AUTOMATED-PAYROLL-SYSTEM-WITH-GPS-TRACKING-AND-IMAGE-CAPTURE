package com.example.workethic.UI.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.workethic.Pojo.*
import com.example.workethic.Retrofit.ServiceBuilder
import com.example.workethic.databinding.FragmentHomeBinding
import com.example.workethic.databinding.FragmentProfileBinding
import com.example.workethic.util.AUNTHETIFICATION
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FragmentProfile : Fragment() {
    var _binding: FragmentProfileBinding? = null
    val binding: FragmentProfileBinding
        get() = _binding!!

    //retrofit view Model
//    private val _mViewModel: MainviewModel by lazy {
//        ViewModelProvider(this, RecViewModelFactory(Repository(ServiceBuilder.api))).get(
//            MainviewModel::class.java
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


//mear implementation


       // create employee_data_object
        var results = mutableListOf<Result>()
        val result = Result(
            Department("Maths"),
            "Vincent",
            "2022-05-19",
            "CCS0093",
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Fmedium.com%2Fnerd-for-tech%2Fintroduction-to-git-and-github-for-beginners-cb52d3ac7d6f&psig=AOvVaw1U1fW3BxQLKw6nWbZhXWZf&ust=1652965331097000&source=images&cd=vfe&ved=0CAwQjRxqFwoTCLDxu-uN6fcCFQAAAAAdAAAAABAI",
            "Trade Marketer",
            "Munene",
            Location("1332423l:236846N", "", ""),
            "Emmanuel",
            Salary(120000, 50000),
            1
        )
        results.add(result)

        //create employee
        val employee_data = Employee(results)
        lifecycleScope.launchWhenCreated {
//            val post = Posts("my commet"
//                ,"Comment",104)


            val response = try {
                ServiceBuilder.api.postToServer(AUNTHETIFICATION,employee_data)

            }catch (e:IOException) {
                Log.d("NORES", "onCreateView: No internet")
                return@launchWhenCreated

            }catch (e:HttpException){
                Log.d("NORES", "onCreateView: http error")
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body()!=null){
                Log.d("MYRES", "onResponse: ${response.body()}\n ${response.code()}")

            }else{
                Log.d("NORES", "onResponse:not successful ${response.message()}")
            }

//            val response = ServiceBuilder.api.postPosts(post)
//            response.enqueue(object:Callback<Posts>{
//                override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
//                    if (response.isSuccessful && response.body()!=null){
//                        Log.d("MYRES", "onResponse: ${response.body()}\n ${response.code()}")
//                    }else{
//                        Log.d("NORES", "onResponse:not successful ")
//                    }
//                }
//
//                override fun onFailure(call: Call<Posts>, t: Throwable) {
//                    Log.d("NORES", "onFailure: failed ${t.message.toString()}")                }
//
//            })
        }


        return binding.root


    }
}