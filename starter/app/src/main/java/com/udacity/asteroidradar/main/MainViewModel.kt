package com.udacity.asteroidradar.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val repository: AsteroidRepository) : ViewModel() {
    val apiKey = "HhE8mIraVrXxh3aGMo17ly49qHnwCAb1yt7PHElH"
    //Picture of Day Status
    private val _pictureOfDayStatus = MutableLiveData<String>()
    val pictureOfDayStatus: LiveData<String> get() = _pictureOfDayStatus

    //Picture Of Day
    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?> get() = _pictureOfDay

    //Period for which asteroids to be retrieved
    var _period = MutableLiveData<Int>()
    val period: LiveData<Int> get() = _period

    //Selected Asteroid
    private var _selectedAsteroid = MutableLiveData<Asteroid?>()
    val selectedAsteroid: LiveData<Asteroid?> get() = _selectedAsteroid

    init{
        _period.value = 0
        getAsteroids()
        getPictureOfDay()
    }
    fun onAsteroidClicked(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _selectedAsteroid.value = null
    }

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allAsteroid: LiveData<List<Asteroid>> = repository.allAsteroids

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(asteroid: Asteroid) = viewModelScope.launch {
        repository.insert(asteroid)
    }

     fun getAsteroids() {
        viewModelScope.launch {
            var status: String = ""

            val formatter = SimpleDateFormat("yyyy-MM-dd")

            val sDate = Calendar.getInstance()
            sDate.time = Date() // Using today's date

            val eDate = Calendar.getInstance()
            eDate.add(Calendar.DATE, period.value!!)

            val startDate = formatter.format(sDate.time)
            val endDate = formatter.format(eDate.time)


            //Retrieve Asteroids through Web Services for Weekly and Today Period
            if (period.value!! >= 0) {
                repository.refreshAsteroids(startDate, endDate, apiKey)

                if (allAsteroid.value?.size!! > 1)
                    status = "Success:${allAsteroid.value?.size}  Mars properties retrieved"
                else
                    status = "Failure"
            }
        }
    }

     fun getPictureOfDay(){
        viewModelScope.launch {
            try {
                var listResult =
                    AsteroidApi.retrofitService.getPictureOfDay(apiKey)

                if (listResult.size > 0) {
                    _pictureOfDay.value = listResult[0]

                    Log.i("Picture of Day","URL is:"+listResult[0].toString())
                }
                _pictureOfDayStatus.value = "Success: ${listResult.size}"
            } catch (e: Exception) {
                _pictureOfDayStatus.value = "Failure: ${e.message}"
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