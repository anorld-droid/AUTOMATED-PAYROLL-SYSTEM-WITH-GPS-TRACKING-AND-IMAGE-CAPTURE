package com.example.workethic.UI.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.workethic.databinding.FragmentHomeBinding
import com.example.workethic.databinding.FragmentProfileBinding

class FragmentProfile:Fragment() {
    var _binding: FragmentProfileBinding?=null
    val binding: FragmentProfileBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)




        return binding.root


    }
}