package com.example.workethic.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.workethic.Pojo.DefineOneTimeWork
import com.example.workethic.Pojo.DefinePeriodicTimeWork
import com.example.workethic.R
import com.example.workethic.Service.ForeGroundService
import com.example.workethic.databinding.ActivityEmployeeBinding
import com.example.workethic.util.STOP_SERVICE
import java.util.concurrent.TimeUnit

class EmployeeActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmployeeBinding
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHost
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_home,
                R.id.fragment_news,
                R.id.fragment_profile
            )
        )

        val toolBar = binding.toolbar
        setSupportActionBar(toolBar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)


       // postToServerPeriodically()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.log_out_menu, menu)
//
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.log_out -> {
//                sendCommandsToService(STOP_SERVICE)
//                ForeGroundService.isLoggedIn.postValue(false)
//                WorkManager.getInstance(applicationContext).cancelAllWorkByTag("postingToServer")
//                Intent(this, LoginActivity::class.java).also {
//                    startActivity(it)
//                }
//            }
//        }
//        return true
//    }

    //send commands
    private fun sendCommandsToService(action: String) {
        Intent(this, ForeGroundService::class.java).also {
            it.action = action
            stopService(it)

        }
    }

    //periodic work
    private fun postToServerPeriodically() {
        val postToServer =
            PeriodicWorkRequest.Builder(DefinePeriodicTimeWork::class.java, 16, TimeUnit.MINUTES)
                .setInitialDelay(16, TimeUnit.MINUTES)
                .addTag("postingToServer")
                .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "postToServer",
            ExistingPeriodicWorkPolicy.KEEP, postToServer
        )
    }

}