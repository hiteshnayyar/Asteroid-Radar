package com.udacity.asteroidradar

import android.app.Application
import androidx.work.*
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.AsteroidRepository
import com.udacity.asteroidradar.worker.RemoveStaleAsteroids
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AsteroidDatabase.getDatabase(this) }
    val repository by lazy { AsteroidRepository(database.asteroidDao) }

    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                //}
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RemoveStaleAsteroids>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RemoveStaleAsteroids.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
}
