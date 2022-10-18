package com.example.happyplacesapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.R
import com.example.happyplacesapp.adapter.HappyPlacesAdapter
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.model.HappyPlaceModel
import com.example.happyplacesapp.utils.SwipeToDeleteCallback
import com.example.happyplacesapp.utils.SwipeToEditCallback

class MainActivity : AppCompatActivity(), OnClickListener {
    companion object {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 0
        const val EXTRA_PLACE_DETAILS = "happyPlaceDetails"
    }

    private var binding: ActivityMainBinding? = null
    private var happyPlaceModels: ArrayList<HappyPlaceModel>? = null
    private var happyPlacesAdapter: HappyPlacesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabActivityMainAdd?.setOnClickListener(this)
        getHappyPlacesData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_activity_main_add -> {
                val intent = Intent(this, AddPlacesActivity::class.java)
                startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
                getHappyPlacesData()
            }
        }
    }

    private fun getHappyPlacesData() {
        val databaseHandler = DatabaseHandler(this)
        happyPlaceModels = databaseHandler.getHappyPlaces()
        if (happyPlaceModels!!.size > 0) {
            binding?.tvActivityMainNote?.visibility = INVISIBLE
            binding?.rvActivityMainList?.visibility = VISIBLE
            setRecyclerView()
        } else {
            binding?.tvActivityMainNote?.visibility = VISIBLE
            binding?.rvActivityMainList?.visibility = INVISIBLE
        }
    }

    private fun setRecyclerView() {
        happyPlacesAdapter = HappyPlacesAdapter(this, happyPlaceModels!!)
        binding?.rvActivityMainList?.adapter = happyPlacesAdapter
        binding?.rvActivityMainList?.setHasFixedSize(true)
        binding?.rvActivityMainList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        happyPlacesAdapter?.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, happyPlaceModel: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, happyPlaceModel)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                happyPlacesAdapter!!.notifyEditItem(
                    this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvActivityMainList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                happyPlacesAdapter!!.deleteItem(viewHolder.adapterPosition)
                getHappyPlacesData()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.rvActivityMainList)
    }
}