package com.example.workethic.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.workethic.Pojo.GetId_statusState
import com.example.workethic.R
import com.example.workethic.Retrofit.ServiceBuilder
import com.example.workethic.Service.ForeGroundService
import com.example.workethic.ViewModel.MainviewModel
import com.example.workethic.ViewModel.RecViewModelFactory
import com.example.workethic.ViewModel.Repository
import com.example.workethic.databinding.ActivityEmployeeBinding
import com.example.workethic.util.AUNTHETIFICATION
import com.example.workethic.util.STOP_SERVICE
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EmployeeActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmployeeBinding
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    //view model
    private val _mViewModel: MainviewModel by lazy {
        ViewModelProvider(this, RecViewModelFactory(Repository(ServiceBuilder.api))).get(
            MainviewModel::class.java
        )
    }

    //map
    val string_map: HashMap<String, RequestBody> = HashMap()


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



        GetId_statusState.statusChangeListener.observe(this) {
            string_map.put("status", RequestBody.create(MultipartBody.FORM, "1"))
            if (it == 1) {
                _mViewModel.updateStatusDetails(
                    AUNTHETIFICATION,
                    GetId_statusState.id,
                    string_map
                )
            } else {
                string_map.put("status", RequestBody.create(MultipartBody.FORM, "0"))
                _mViewModel.updateStatusDetails(
                    AUNTHETIFICATION,
                    GetId_statusState.id,
                    string_map
                )

            }
        }


    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.log_out_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out -> {
                sendCommandsToService(STOP_SERVICE)
                ForeGroundService.isLoggedIn.postValue(false)
                GetId_statusState.statusChangeListener.postValue(0)
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }

    //send commands
    private fun sendCommandsToService(action: String) {
        Intent(this, ForeGroundService::class.java).also {
            it.action = action
            stopService(it)

        }
    }

}