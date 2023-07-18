package com.udacity.asteroidradar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.main.AsteroidViewModelFactory
import com.udacity.asteroidradar.main.MainViewModel

class MainActivity : AppCompatActivity() {
//    private val asteroidViewModel: MainViewModel by viewModels {
//        AsteroidViewModelFactory((application as AsteroidApplication).repository)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
