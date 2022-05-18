package com.example.workethic.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workethic.Pojo.Employee
import kotlinx.coroutines.launch

class MainviewModel(
    private val repository: Repository
) : ViewModel() {

    //method to post
    fun postToServer(employeeData: Employee){
        viewModelScope.launch {
            repository.postToServer(employeeData)
        }
    }

}