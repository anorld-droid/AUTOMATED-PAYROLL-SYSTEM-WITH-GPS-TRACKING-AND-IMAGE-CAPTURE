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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)



        return binding.root


    }
}