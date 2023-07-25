package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the Asteroid class with Room.
 */
@Dao
interface AsteroidDatabaseDao {
    //Insert List of Asteroids retrieved from the Web Services
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg asteroids: Asteroid)

    //Retrieve List of Asteroids ordered by Approach Date for Main Fragment
    @Query("SELECT * FROM asteroid_table WHERE CASE WHEN :period =-1 THEN 1=1 ELSE " +
            "((JULIANDAY(close_approach_date)-JULIANDAY(DATE('now')))<=:period) AND " +
            "((JULIANDAY(close_approach_date)-JULIANDAY(DATE('now')))>=0) END " +
            "ORDER BY date(close_approach_date) DESC")
    fun getAsteroids(period: Int): LiveData<List<Asteroid>>

    //Delete old Asteroids
    @Query("DELETE FROM asteroid_table WHERE date(close_approach_date)<=date('now')")
    suspend fun removeStaleAsteroids()

    //Retrieve selected Asteroid for Detail Fragment
    @Query("SELECT * from asteroid_table WHERE id = :key")
    suspend fun getAsteroid(key: Long): Asteroid?

}