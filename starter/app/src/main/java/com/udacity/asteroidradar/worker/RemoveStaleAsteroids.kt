package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.AsteroidRepository
import retrofit2.HttpException

class RemoveStaleAsteroids(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "workManager"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database.asteroidDao)

        return try {
            repository.removeStaleAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
