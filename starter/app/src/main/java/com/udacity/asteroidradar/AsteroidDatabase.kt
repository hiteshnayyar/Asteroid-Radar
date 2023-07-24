package com.udacity.asteroidradar

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Asteroid::class], version = 5, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDatabaseDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AsteroidDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AsteroidDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AsteroidDatabase::class.java,
                    "asteroid_database"
                ).fallbackToDestructiveMigration()
                .addCallback(AsteroidDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


    private class AsteroidDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.asteroidDao)
                }
            }
        }

        suspend fun populateDatabase(asteroidDatabaseDao: AsteroidDatabaseDao) {
            /*
            // Add sample words
            var asteroid = Asteroid(1,"Hitesh","10/07/2023",5.7,1000.0,56.0,110.0,false)
            asteroidDatabaseDao.insert(asteroid)
            Log.i("AsteroidDatabase","Added 1st Asteroid")

            asteroid = Asteroid(2,"Varinder","21/07/2023",6.0,90.0,100.0,10.0,false)
            asteroidDatabaseDao.insert(asteroid)
            Log.i("AsteroidDatabase","Added 2nd Asteroid")
            asteroid = Asteroid(3,"Sarbjit","25/07/2023",2.0,105.0,50.0,20.0,false)
            asteroidDatabaseDao.insert(asteroid)
            Log.i("AsteroidDatabase","Added 3rd Asteroid")
            asteroid = Asteroid(4,"Ranjan","28/07/2023",3.4,130.0,550.0,30.0,false)
            asteroidDatabaseDao.insert(asteroid)
            Log.i("AsteroidDatabase","Added 4th Asteroid")
            asteroid = Asteroid(5,"Gurpreet","30/07/2023",5.6,120.0,110.0,40.0,false)
            asteroidDatabaseDao.insert(asteroid)
            Log.i("AsteroidDatabase","Added 5th Asteroid")
            */

        }
    }
}