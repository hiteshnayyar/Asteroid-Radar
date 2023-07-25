package com.udacity.asteroidradar

import androidx.annotation.WorkerThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.main.WebServiceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AsteroidRepository(private val asteroidDao: AsteroidDatabaseDao) {
    //Asteroid List to be observed and by Default, all asteroids in the DB will be shown

    //var asteroidList: LiveData<List<Asteroid>> = asteroidDao.getAsteroids(7)
    var _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>> get() = _asteroidList

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun refreshAsteroids(startDate: String, endDate: String, period: Int, apiKey: String) {
        withContext(Dispatchers.IO) {
            //Retrieve Asteroids from the Web Services and Save in the repository
            var jsonStr =
                AsteroidApi.retrofitService.getAsteroids(startDate, endDate, apiKey)

            var asteroids = parseAsteroidsJsonResult(JSONObject(jsonStr))
            asteroidDao.insert(asteroids)
            _asteroidList = asteroidDao.getAsteroids(period)
        }
    }
}
