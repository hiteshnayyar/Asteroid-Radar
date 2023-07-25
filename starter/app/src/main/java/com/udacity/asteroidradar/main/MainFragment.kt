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
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    //    private val viewModel: MainViewModel by lazy {
//        ViewModelProvider(this).get(MainViewModel::class.java)
//    }
    private val viewModel: MainViewModel by viewModels {
        AsteroidViewModelFactory((requireNotNull(this.activity).application as AsteroidApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.asteroidRecycler

        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { selectedAsteroid ->
            viewModel.onAsteroidClicked(selectedAsteroid)
            Toast.makeText(context, "${selectedAsteroid.id}", Toast.LENGTH_LONG).show()
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.selectedAsteroid.observe(this, Observer { selectedAsteroid ->
            selectedAsteroid?.let {
                this.findNavController()
                    .navigate(MainFragmentDirections.actionShowDetail(selectedAsteroid))
                viewModel.onAsteroidDetailNavigated()
            }
        })

        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            var pic = viewModel.pictureOfDay.value
            if (viewModel.asteroids.value?.size!! > 1 && pic != null) {
                it?.let {
                    adapter.addHeaderAndSubmitList(it, listOf(pic) as List<PictureOfDay>)
                }
            }
        })

        viewModel.period.observe(viewLifecycleOwner, Observer {
            viewModel.getAsteroids()
        })

        //Add Logout Menu Option
        val menuHost: MenuHost = requireActivity()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_overflow_menu, menu)
            }

            //Setup Navigation to Login Fragment on tapping Logout
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.show_all_menu -> {
                        viewModel._period.value = 7
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
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }
}
