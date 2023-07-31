package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.data.Asteroid
import com.udacity.asteroidradar.data.AsteroidRepository
import com.udacity.asteroidradar.data.Constants
import com.udacity.asteroidradar.data.PictureOfDay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

enum class WebServiceStatus { LOADING, ERROR, DONE }
class MainViewModel(private val repository: AsteroidRepository) : ViewModel() {
   val apiKey = BuildConfig.NASA_API_KEY

    //Asteroid Web Service Status
    private val _asteroidStatus = MutableLiveData<WebServiceStatus>()
    val asteroidStatus: LiveData<WebServiceStatus> get() = _asteroidStatus

    //Picture of Day Web Service Status
    private val _pictureOfDayStatus = MutableLiveData<WebServiceStatus>()
    val pictureOfDayStatus: LiveData<WebServiceStatus> get() = _pictureOfDayStatus

    //Period for which asteroids to be retrieved
    var _period = MutableLiveData<Int>()
    val period: LiveData<Int> get() = _period


    //Selected Asteroid
    private var _selectedAsteroid = MutableLiveData<Asteroid?>()
    val selectedAsteroid: LiveData<Asteroid?> get() = _selectedAsteroid

    init {
        _asteroidStatus.value = WebServiceStatus.LOADING
        _pictureOfDayStatus.value = WebServiceStatus.LOADING
        _period.value = Constants.DEFAULT_END_DATE_DAYS -1
    }

    //Retrieves List of Asteroids
    var asteroids = repository.asteroidList

    //Picture Of Day
    val pictureOfDay: LiveData<PictureOfDay?> = repository.pictureOfDay

    fun onAsteroidClicked(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _selectedAsteroid.value = null
    }

    //Retrieve Asteroids from Repository
    fun getAsteroids() {
        viewModelScope.launch {
            _asteroidStatus.value = WebServiceStatus.LOADING
            try {
                if ((period.value ?: 0) >= 0) {
                    //Retrieve Asteroids for Current Day/Week
                    val formatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

                    val sDate = Calendar.getInstance()
                    sDate.time = Date() // Using today's date

                    val eDate = Calendar.getInstance()
                    eDate.add(Calendar.DAY_OF_MONTH, period.value ?: 0)

                    val startDate = formatter.format(sDate.time)
                    val endDate = formatter.format(eDate.time)

                    //Retrieve Asteriods from Repository
                    repository.retrieveSpecificAsteroids(
                        startDate,
                        endDate,
                        period.value ?: 0,
                        apiKey
                    )

                } else
                    repository.retrieveAllAsteroids(period.value ?: 0)

                //Whether Asteroids were available in the Repository
                if ((asteroids.value?.size ?: 0) > 0)
                    _asteroidStatus.value = WebServiceStatus.DONE
                else
                    _asteroidStatus.value = WebServiceStatus.ERROR

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                _asteroidStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel","Network Error")
            } catch (e: Exception) {
                //Exceptions to be handled separately for I/O or others
                _asteroidStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel","Other Error")
            }
        }
    }

    //Retrieve Picture of Day from Repository
    fun getPictureOfDay() {
        viewModelScope.launch {
            _pictureOfDayStatus.value = WebServiceStatus.LOADING
            try {
                repository.retrievePictureOfDay(apiKey)
                //Whether Asteroids were available in the Repository
                if (pictureOfDay.value != null)
                    _pictureOfDayStatus.value = WebServiceStatus.DONE
                else
                    _pictureOfDayStatus.value = WebServiceStatus.ERROR
            } catch (networkError: IOException) {
                if (pictureOfDay.value != null)
                    _pictureOfDayStatus.value = WebServiceStatus.DONE
                else
                    _pictureOfDayStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel-PoD", "Network Issue")
            } catch (e: Exception) {
                //Exceptions to be handled separately for I/O or others
                if (pictureOfDay.value != null)
                    _pictureOfDayStatus.value = WebServiceStatus.DONE
                else
                    _pictureOfDayStatus.value = WebServiceStatus.ERROR
                Log.i("MainViewModel-PoD", e.toString())
            }
        }
    }
}

//Setup Factory for View Model

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