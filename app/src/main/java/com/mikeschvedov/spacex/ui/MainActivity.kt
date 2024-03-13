package com.mikeschvedov.spacex.ui


import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mikeschvedov.spacex.R
import com.mikeschvedov.spacex.databinding.ActivityMainBinding
import com.mikeschvedov.spacex.work.DBUpdateWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var mainViewModel: MainViewModel

    private var snackBar: Snackbar? = null

    private var isInternetAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_ships, R.id.nav_launches
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        mainViewModel.liveNetworkMonitor.observe(this, Observer {
            showNetworkMessage(it)
            isInternetAvailable = it
        })

        // If we returned to this activity as a result of clicking the notification.
        // We want to go straight to the LaunchesFragment.
        val stringExtraValue = intent.extras?.getString(DBUpdateWorker.CLICKED_EXTRA_NAME)
        if(stringExtraValue == DBUpdateWorker.CLICKED_EXTRA_VALUE){
            navController.navigate(R.id.action_nav_ships_to_nav_launches)
        }

        // Starting the Work Manager
        runWorkerOnFirstLunch()
    }


    private fun runWorkerOnFirstLunch() {
        // We always check if the worker was already created
        // Because maybe the first time we ran the app there was no internet,
        //So we need to keep trying the next time as well.
        // Only when the worker is created we stop trying.
        // Also save in shared pref a Boolean that keeps track of "isFirstTimeCreatingWorker"
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val isFirstTimeCreatingWorker =
            sharedPref.getBoolean(
                getString(R.string.is_worker_activated),
                true
            ) // the default is false
        if (isFirstTimeCreatingWorker) {
            // Check if there is an internet connection
            if (mainViewModel.networkStatusChecker.hasInternetConnection()) {
                // If its the first time running the app, initiate the worker
                requestWork()
                // Set shared pref that it is no longer first time
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.is_worker_activated), false)
                    apply()
                }
            }
        }
    }

    private fun requestWork() {
        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<DBUpdateWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(workDataOf(DBUpdateWorker.EXTRA_VALUE to 300)) // passing a parameter
            .build()

        workManager
            .enqueue(request)

        workManager
            .getWorkInfoByIdLiveData(request.id).observe(this) {
                val text = when (it.state) {
                    WorkInfo.State.ENQUEUED -> "ENQUEUED"
                    WorkInfo.State.RUNNING -> "RUNNING"
                    WorkInfo.State.SUCCEEDED -> {
                        // Getting optional output data from the worker
                        val out = it.outputData.getString(DBUpdateWorker.OUTPUT_EXTRA)
                            ?: "No Output"
                        "SUCCEEDED $out"
                    }
                    WorkInfo.State.FAILED -> "FAILED"
                    WorkInfo.State.BLOCKED -> "BLOCKED" // Constraints failed
                    WorkInfo.State.CANCELLED -> "CANCELLED"
                }
                println("Worker State: $text")
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                binding.root,
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }
}