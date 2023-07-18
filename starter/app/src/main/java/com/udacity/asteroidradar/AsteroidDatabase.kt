package com.udacity.asteroidradar

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Asteroid::class], version = 2, exportSchema = false)
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
            // Delete all content here.
            //asteroidDatabaseDao.deleteAll()

            // Add sample words.
            Log.i("Populate Asteroid Database","Added Asteroid")
            var asteroid = Asteroid(1,"Hello","",0.0,0.0,0.0,0.0,false)
            asteroidDatabaseDao.insert(asteroid)
        }
    }
}