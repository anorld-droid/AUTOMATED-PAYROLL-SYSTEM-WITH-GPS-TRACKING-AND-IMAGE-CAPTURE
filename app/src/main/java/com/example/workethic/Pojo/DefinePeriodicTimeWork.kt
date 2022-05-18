package com.example.workethic.Pojo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class DefinePeriodicTimeWork(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
// TODO: function to see location of user 
            // TODO: function to post regularly

            for (i in 1..100000) {
                Log.d("NEIMP", "doWork: $i")
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()

        }
    }
}