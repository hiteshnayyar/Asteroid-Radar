package com.udacity.asteroidradar

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "asteroid_table")
data class Asteroid(
    @PrimaryKey(autoGenerate=true)
    val id: Long = 0L,

    @ColumnInfo(name="code_name")
    val codename: String,

    @ColumnInfo(name="close_approach_date")
    val closeApproachDate: String = "",

    @ColumnInfo(name="absolute_magnitude")
    val absoluteMagnitude: Double = 0.0,

    @ColumnInfo(name="estimated_diameter")
    val estimatedDiameter: Double = 0.0,

    @ColumnInfo(name="relative_velocity")
    val relativeVelocity: Double = 0.0,

    @ColumnInfo(name="distance_from_earth")
    val distanceFromEarth: Double = 0.0,

    @ColumnInfo(name="is_potentially_hazardous")
    val isPotentiallyHazardous: Boolean = false) : Parcelable