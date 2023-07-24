package com.udacity.asteroidradar

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AsteroidRepository (private val asteroidDao:AsteroidDatabaseDao){

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allAsteroids: LiveData<List<Asteroid>> = asteroidDao.getAllAsteroids()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(asteroid: Asteroid) {
        asteroidDao.insert(asteroid)
    }
    suspend fun refreshAsteroids(startDate: String, endDate: String, apiKey: String){
        withContext(Dispatchers.IO) {
            //Clear Asteroids for Weekly/Today Selections
            asteroidDao.clearAllAsteroids()
            try {

                var jsonStr =
                    AsteroidApi.retrofitService.getAsteroids(startDate, endDate, apiKey)

                var asteroidList = parseAsteroidsJsonResult(JSONObject(jsonStr))
                asteroidDao.insertAll(asteroidList)
            } catch (e: Exception) {
                //_responseStatus.value = "Failure: ${e.message}"
            }

        }
    }
}