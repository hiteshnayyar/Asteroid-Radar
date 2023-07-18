package com.udacity.asteroidradar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AsteroidAdapter : ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.codename)
    }

    class AsteroidViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val asteroidItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            asteroidItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): AsteroidViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.asteroid_recycler, parent, false)
                return AsteroidViewHolder(view)
            }
        }
    }

    class AsteroidsComparator : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.codename == newItem.codename
        }
    }
}