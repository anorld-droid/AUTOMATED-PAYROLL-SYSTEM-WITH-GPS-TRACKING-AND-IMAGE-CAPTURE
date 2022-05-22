package com.example.workethic.Pojo

data class Result(
    val department: Department,
    val first_name: String,
    val hire_date: String,
    val id: String,
    val image: String?,
    val job_name: String,
    val last_name: String,
    val location: Location,
    val owner: String,
    val salary: Salary,
    val status: Int
)