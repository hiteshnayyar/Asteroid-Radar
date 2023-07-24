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
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroid: Asteroid)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<Asteroid>)

    //Retrieve List of Asteroids ordered by Approach Date for Main Fragment
    @Query("SELECT * FROM asteroid_table ORDER BY date(close_approach_date) DESC")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    //Delete old Asteroids
    @Query("DELETE FROM asteroid_table WHERE date(close_approach_date)<=date('now')")
    suspend fun clearStaleAsteroids()

    //Delete all Asteroids
    @Query("DELETE FROM asteroid_table")
    suspend fun clearAllAsteroids()

    //Retrieve selected Asteroid for Detail Fragment
    @Query("SELECT * from asteroid_table WHERE id = :key")
    suspend fun getAsteroid(key: Long): Asteroid?

}