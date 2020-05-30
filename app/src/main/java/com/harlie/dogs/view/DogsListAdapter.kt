package com.harlie.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.util.getProgressDrawable
import com.harlie.dogs.util.loadImage
import kotlinx.android.synthetic.main.item_dogs.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogsListAdapter.DogViewHolder>() {
    private val _tag = "LEE: <" + DogsListAdapter::class.java.simpleName + ">"

    fun updateDogList(newDogList: List<DogBreed>) {
        Timber.tag(_tag).d("updateDogList")
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        //Timber.tag(_tag).d("onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_dogs, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount() = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        //Timber.tag(_tag).d("onBindViewHolder")
        holder.view.dogName.text = dogsList[position].breedName
        holder.view.dogLifespan.text = dogsList[position].breedLifespan
        holder.view.setOnClickListener {view ->
            Timber.tag(_tag).d("-CLICK- $holder")
            val action = ListFragmentDirections.actionListFragmentToDetailFragment()
            action.dogUuid = dogsList[position].uuid
            view.findNavController().navigate(action)
        }
        holder.view.dogListImage.loadImage(dogsList[position].breedImageUrl, getProgressDrawable(holder.view.dogListImage.context))
    }

    class DogViewHolder(var view: View): RecyclerView.ViewHolder(view)

}
