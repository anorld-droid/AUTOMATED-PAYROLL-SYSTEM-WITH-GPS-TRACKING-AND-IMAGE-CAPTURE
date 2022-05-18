package com.example.workethic.Pojo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workethic.Service.ForeGroundService

class DefineOneTimeWork(cont:Context, params: WorkerParameters): Worker(cont,params) {
    override fun doWork(): Result {
        try {
            for (i in 1..100000){
                Log.d("IMP", "doWork: $i")
            }

            return Result.success()
        }catch (e:Exception) {
            return Result.failure()
        }
    }
}