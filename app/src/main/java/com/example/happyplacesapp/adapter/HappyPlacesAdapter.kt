package com.example.happyplacesapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.activity.AddPlacesActivity
import com.example.happyplacesapp.activity.MainActivity
import com.example.happyplacesapp.databinding.ItemHappyPlaceBinding
import com.example.happyplacesapp.model.HappyPlaceModel

class HappyPlacesAdapter(
    val context: Context,
    private val happyPlaceModels: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

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
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, happyPlaceModel)
            }
        }
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

    interface OnClickListener {
        fun onClick(position: Int, happyPlaceModel: HappyPlaceModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddPlacesActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceModels[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
}