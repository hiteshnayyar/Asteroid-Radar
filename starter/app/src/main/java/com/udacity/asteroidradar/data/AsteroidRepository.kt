package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.udacity.asteroidradar.NASAWebServices
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
class AsteroidRepository(private val asteroidDao: AsteroidDatabaseDao) {

    private val period = MutableLiveData<Int>()

    var asteroidList: LiveData<List<Asteroid>> = period.switchMap { period ->
        asteroidDao.getAsteroids(period)
    }
    //Picture Of Day
    private var _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?> get() = _pictureOfDay

    suspend fun retrieveSpecificAsteroids(
        startDate: String,
        endDate: String,
        days: Int,
        apiKey: String) {
        withContext(Dispatchers.IO) {
            //Retrieve Asteroids from the Web Services and Save in the repository
            val jsonStr =
                NASAWebServices.asteroidService.getAsteroids(startDate, endDate, apiKey)

            val asteroids = parseAsteroidsJsonResult(JSONObject(jsonStr))
            asteroidDao.insert(*asteroids.toTypedArray())
            period.postValue(days)
        }
    }

    fun retrieveAllAsteroids(days: Int) {
        period.postValue(days)
    }

    suspend fun retrievePictureOfDay(apiKey: String) {
        withContext(Dispatchers.IO) {
            //Retrieve Picture of Day from the Web Services
            val pic = NASAWebServices.pictureOfDayService.getPictureOfDay(apiKey)
            if (pic != null) {
                _pictureOfDay.postValue(pic)
            }
        }
    }

    suspend fun removeStaleAsteroids(){
        withContext(Dispatchers.IO) {
            asteroidDao.removeStaleAsteroids()
        }
    }
}
