package com.udacity.asteroidradar.worker

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.AsteroidRepository
import com.udacity.asteroidradar.data.Constants
import com.udacity.asteroidradar.data.Constants.DEFAULT_END_DATE_DAYS
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class DownloadAsteroids(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    val apiKey = BuildConfig.NASA_API_KEY

    companion object {
        const val WORK_NAME = "workManager"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database.asteroidDao)

        return try {
            val formatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            val sDate = Calendar.getInstance()
            sDate.time = Date() // Using today's date

            //Setup End Date to be 7 days from Current Date
            val eDate = Calendar.getInstance()
            eDate.time = Date()
            eDate.add(Calendar.DAY_OF_MONTH, DEFAULT_END_DATE_DAYS-1)

            val startDate = formatter.format(sDate.time)
            val endDate = formatter.format(eDate.time)

            repository.retrieveSpecificAsteroids(startDate, endDate, 6, apiKey as String)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
