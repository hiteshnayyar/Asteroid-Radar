package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.data.Asteroid
import com.udacity.asteroidradar.data.Constants
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//Web Service URI: https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY

fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    //Retrieve Object Near Earth Objects
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()
    //Setup Formatted Dates based on Start and End Dates
    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    //Parse JSON Object to retrieve Asteroids
    for (formattedDate in nextSevenDaysFormattedDates) {
        if (nearEarthObjectsJson.has(formattedDate)) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)
            //Loop through JSON Object Array to retrieve Asteriod from each JSON object
            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = Asteroid(id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)

                //Append asteroid to the List
                asteroidList.add(asteroid)
            }
        }
    }

    return asteroidList
}


//fun parsePictureOfDayJsonResult(jsonResult: JSONObject): PictureOfDay? {
//
//        val url = jsonResult.getString("hdurl")
//        val title = jsonResult.getString("title")
//        val mediatype = jsonResult.getString("media_type")
//        if (mediatype == "image")
//            return PictureOfDay(mediatype,title,url)
//        else
//            return null
//}


private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}
