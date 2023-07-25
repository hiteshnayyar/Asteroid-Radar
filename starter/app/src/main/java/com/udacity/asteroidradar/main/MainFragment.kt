package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.AsteroidAdapter
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        AsteroidViewModelFactory((requireNotNull(this.activity).application as AsteroidApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Get Recyclerview from Binding Object
        val recyclerView = binding.asteroidRecycler

        //Setup Listener for Recyclerview Adapter
        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { selectedAsteroid ->
            viewModel.onAsteroidClicked(selectedAsteroid)
            Toast.makeText(context, "${selectedAsteroid.id}", Toast.LENGTH_LONG).show()
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //Observe Selected Asteroid and Setup Navigation from Main Fragment to Detail Fragment on Change
        viewModel.selectedAsteroid.observe(viewLifecycleOwner, Observer { selectedAsteroid ->
            selectedAsteroid?.let {
                if (null != it) {
                    this.findNavController()
                        .navigate(MainFragmentDirections.actionShowDetail(selectedAsteroid))
                    viewModel.onAsteroidDetailNavigated()
                }
            }
        })

        //Observe Asteroids List to refresh Recyclerview
        viewModel.asteroids.observe(this, Observer {
            val pic = viewModel.pictureOfDay.value
            if (((viewModel.asteroids.value?.size?:0) > 0) && (pic != null)) {
                it?.let {
                    adapter.addHeaderAndSubmitList(it, listOf(pic))
                }
            }
        })

        //Observe Period for which Asteroids to be retrieved and refresh Recyclerview
        viewModel.period.observe(viewLifecycleOwner, Observer {
            //Refresh Picture of Day
            viewModel.getPictureOfDay()

            //Refresh Asteroids List
            viewModel.getAsteroids()
        })


        //Setup Overlay Menu
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_overflow_menu, menu)
            }

            //Setup Navigation to Login Fragment on tapping Logout
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.show_all_menu -> {
                        viewModel._period.value = 6
                        true
                    }
                    R.id.show_rent_menu -> {
                        viewModel._period.value = 0
                        true
                    }
                    R.id.show_buy_menu -> {
                        viewModel._period.value = -1
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }
}
