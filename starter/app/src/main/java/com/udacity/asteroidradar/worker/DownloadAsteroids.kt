package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.AsteroidRepository
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class DownloadAsteroids(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    val apiKey = "HhE8mIraVrXxh3aGMo17ly49qHnwCAb1yt7PHElH"

    companion object {
        const val WORK_NAME = "workManager"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database.asteroidDao)

        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd")

            val sDate = Calendar.getInstance()
            sDate.time = Date() // Using today's date

            val eDate = Calendar.getInstance()
            eDate.time = Date()

            val startDate = formatter.format(sDate.time)
            val endDate = formatter.format(eDate.time)

            repository.retrieveSpecificAsteroids(startDate, endDate, 0, apiKey)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
