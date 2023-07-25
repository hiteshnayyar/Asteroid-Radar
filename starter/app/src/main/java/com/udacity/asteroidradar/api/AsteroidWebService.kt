package com.udacity.asteroidradar

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.data.Constants.BASE_URL
import com.udacity.asteroidradar.data.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofitAsteroids = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitPictureOfDay = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AsteroidWebService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("start_date") startDate:String,
                     @Query("end_date") endDate:String,
                     @Query("api_key")  apiKey:String): String

    @GET("planetary/apod")
    suspend fun getPictureOfDay(@Query("api_key") apiKey: String):PictureOfDay
}

object NASAWebServices {
    val asteroidService : AsteroidWebService by lazy {
        retrofitAsteroids.create(AsteroidWebService::class.java)
    }

    val pictureOfDayService : AsteroidWebService by lazy {
        retrofitPictureOfDay.create(AsteroidWebService::class.java)
    }

}

