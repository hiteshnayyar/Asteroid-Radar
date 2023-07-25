package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.api.parsePictureOfDayJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

enum class WebServiceStatus { LOADING, ERROR, DONE }
class MainViewModel(private val repository: AsteroidRepository) : ViewModel() {
    val apiKey = "HhE8mIraVrXxh3aGMo17ly49qHnwCAb1yt7PHElH"

    //Asteroid Web Service Status
    private val _AsteroidStatus = MutableLiveData<WebServiceStatus>()
    val AsteroidStatus: LiveData<WebServiceStatus> get() = _AsteroidStatus

    //Picture of Day Web Service Status
    private val _pictureOfDayStatus = MutableLiveData<WebServiceStatus>()
    val pictureOfDayStatus: LiveData<WebServiceStatus> get() = _pictureOfDayStatus

    //Picture Of Day
    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?> get() = _pictureOfDay

    //Period for which asteroids to be retrieved
    var _period = MutableLiveData<Int>()
    val period: LiveData<Int> get() = _period

    //Retrieves List of Asteroids
    val asteroids = repository.asteroidList
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    //Selected Asteroid
    private var _selectedAsteroid = MutableLiveData<Asteroid?>()
    val selectedAsteroid: LiveData<Asteroid?> get() = _selectedAsteroid

    init {
        _period.value = 0
        getAsteroids()
        refreshPictureOfDay()
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _selectedAsteroid.value = null
    }


    fun getAsteroids() {
        viewModelScope.launch {
            _AsteroidStatus.value = WebServiceStatus.LOADING
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd")

                val sDate = Calendar.getInstance()
                sDate.time = Date() // Using today's date

                val eDate = Calendar.getInstance()
                eDate.add(Calendar.DATE, period.value!!)

                val startDate = formatter.format(sDate.time)
                val endDate = formatter.format(eDate.time)


                //Retrieve Asteroids through Web Services for Weekly and Today Period
                if (period.value!! >= 0) {
                    repository.refreshAsteroids(startDate, endDate, period.value!!, apiKey)

                    if (asteroids.value?.size!! > 1)
                        _AsteroidStatus.value = WebServiceStatus.DONE
                    else
                        _AsteroidStatus.value = WebServiceStatus.ERROR
                }

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(asteroids.value.isNullOrEmpty())
                    _AsteroidStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel","Network Issue")
            }
            catch (e: Exception) {
                //Exceptions to be handled separately for I/O or others
                if(asteroids.value.isNullOrEmpty())
                    _AsteroidStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel",e.toString())
            }
        }
    }


    private fun refreshPictureOfDay() {
        viewModelScope.launch {
            _pictureOfDayStatus.value = WebServiceStatus.LOADING
            try {
                var jsonStr = AsteroidApi.retrofitService.getPictureOfDay(apiKey)
                _pictureOfDay.value = parsePictureOfDayJsonResult(JSONObject(jsonStr))
                if(_pictureOfDay != null)
                    _pictureOfDayStatus.value = WebServiceStatus.DONE
                else
                    _pictureOfDayStatus.value = WebServiceStatus.ERROR
            } catch (e: Exception) {
                _pictureOfDayStatus.value = WebServiceStatus.ERROR
            }
        }
    }
}


class AsteroidViewModelFactory(private val repository: AsteroidRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}