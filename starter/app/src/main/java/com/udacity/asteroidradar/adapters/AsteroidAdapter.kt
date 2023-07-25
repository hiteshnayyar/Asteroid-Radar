package com.udacity.asteroidradar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.data.Asteroid
import com.udacity.asteroidradar.data.PictureOfDay
import com.udacity.asteroidradar.databinding.AsteroidRecyclerBinding
import com.udacity.asteroidradar.databinding.HeaderRecyclerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1
class AsteroidAdapter(val clickListener: AsteroidListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(AsteroidsComparator()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<Asteroid>?, header: List<PictureOfDay>) {
        adapterScope.launch {
            val items = when (list) {
                null -> header.map{null}
                else -> header.map{DataItem.Header (it)} + list.map {DataItem.AsteroidItem (it)}
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.create(parent)
            ITEM_VIEW_TYPE_ITEM -> AsteroidViewHolder.create(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            //is DataItem.AsteroidItem -> ITEM_VIEW_TYPE_ITEM
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AsteroidViewHolder -> {
                val asteroidItem = getItem(position) as DataItem.AsteroidItem
                holder.bind(clickListener,asteroidItem.asteroid)
            }
            is HeaderViewHolder ->{
                val header = getItem(position) as DataItem.Header
                holder.bind(header.pictureOfDay)
            }
        }
    }


    class AsteroidViewHolder private constructor (val binding: AsteroidRecyclerBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AsteroidListener,item: Asteroid) {
            binding.asteroid = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun create(parent: ViewGroup): AsteroidViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidRecyclerBinding.inflate(layoutInflater,parent,false)
                return AsteroidViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder private constructor (val binding: HeaderRecyclerBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureOfDay) {
            binding.header = item
            binding.executePendingBindings()
        }
        companion object {
            fun create(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderRecyclerBinding.inflate(layoutInflater,parent,false)
                return HeaderViewHolder(binding)
            }
        }
    }

    class AsteroidsComparator : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    class AsteroidListener(val clickListener: (selectedAsteroid: Asteroid) -> Unit) {
        fun onClick(selectedAsteroid: Asteroid) = clickListener(selectedAsteroid)
    }
}
sealed class DataItem {
    data class AsteroidItem(val asteroid: Asteroid): DataItem() {
        override val id = asteroid.id
    }

    data class Header(val pictureOfDay: PictureOfDay): DataItem(){
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}