package com.udacity.asteroidradar

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.data.Constants.BASE_URL
import com.udacity.asteroidradar.data.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


var okHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

//Setup Retrofit for Asteroids Web Services with Scalar Convertor
private val retrofitAsteroids = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//Setup Retrofit for Picture of Day Web Services with Moshi Convertor
private val retrofitPictureOfDay = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AsteroidWebService {
    //Setup Interface to retrieve Asteroids based on Start and End Date
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("start_date") startDate:String,
                     @Query("end_date") endDate:String,
                     @Query("api_key")  apiKey:String): String

    //Setup Interface to retrieve Picture of Day
    @GET("planetary/apod")
    suspend fun getPictureOfDay(@Query("api_key") apiKey: String):PictureOfDay
}

object NASAWebServices {
    //Create Object for Asteroid WebService
    val asteroidService : AsteroidWebService by lazy {
        retrofitAsteroids.create(AsteroidWebService::class.java)
    }

    //Create Object for Picture of Day WebService
    val pictureOfDayService : AsteroidWebService by lazy {
        retrofitPictureOfDay.create(AsteroidWebService::class.java)
    }

}

