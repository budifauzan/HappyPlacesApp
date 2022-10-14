package com.example.happyplacesapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.databinding.ItemHappyPlaceBinding
import com.example.happyplacesapp.model.HappyPlaceModel

class HappyPlacesAdapter(
    private val happyPlaceModels: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHappyPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val happyPlaceModel = happyPlaceModels[position]
        holder.civIcon.setImageURI(Uri.parse(happyPlaceModel.image))
        holder.tvTitle.text = happyPlaceModel.title
        holder.tvDescription.text = happyPlaceModel.description
    }

    override fun getItemCount(): Int {
        return happyPlaceModels.size
    }

    class ViewHolder(itemHappyPlaceBinding: ItemHappyPlaceBinding) :
        RecyclerView.ViewHolder(itemHappyPlaceBinding.root) {
        val civIcon = itemHappyPlaceBinding.civItemHappyPlaceIcon
        val tvTitle = itemHappyPlaceBinding.tvItemHappyPlaceTitle
        val tvDescription = itemHappyPlaceBinding.tvItemHappyPlaceDescription
    }

}