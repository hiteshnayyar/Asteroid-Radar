package com.udacity.asteroidradar.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "asteroid_table")
data class Asteroid(
    @PrimaryKey (autoGenerate=true)
    var id: Long = 0L,

    // used to map img_src from the JSON to imgSrcUrl in our class
    @ColumnInfo(name="code_name")
    var codename: String,

    @ColumnInfo(name="close_approach_date")
    var closeApproachDate: String = "",

    @ColumnInfo(name="absolute_magnitude")
    var absoluteMagnitude: Double = 0.0,

    @ColumnInfo(name="estimated_diameter")
    var estimatedDiameter: Double = 0.0,

    @ColumnInfo(name="relative_velocity")
    var relativeVelocity: Double = 0.0,

    @ColumnInfo(name="distance_from_earth")
    var distanceFromEarth: Double = 0.0,

    @ColumnInfo(name="is_potentially_hazardous")
    var isPotentiallyHazardous: Boolean = false) : Parcelable