package com.udacity.asteroidradar

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the Asteroid class with Room.
 */
@Dao
interface AsteroidDatabaseDao {

    //Insert Asteroids
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroid: Asteroid)

    //Retrieve List of Asteroids ordered by Approach Date for Main Fragment
    @Query("SELECT * FROM asteroid_table ORDER BY date(close_approach_date)")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    //Delete old Asteroids for Work Manager
    @Query("DELETE FROM asteroid_table WHERE date(close_approach_date)<=date('now')")
    suspend fun clear()

    //Retrieve selected Asteroid for Detail Fragment
    @Query("SELECT * from asteroid_table WHERE id = :key")
    suspend fun getAsteroid(key: Long): Asteroid?

}