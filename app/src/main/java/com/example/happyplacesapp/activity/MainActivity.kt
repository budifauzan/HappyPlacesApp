package com.example.happyplacesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.model.HappyPlaceModel

class MainActivity : AppCompatActivity(), OnClickListener {
    private var binding: ActivityMainBinding? = null
    private var happyPlaceModels: ArrayList<HappyPlaceModel>? = null

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
                startActivity(intent)
            }
        }
    }

    private fun getHappyPlacesData() {
        val databaseHandler = DatabaseHandler(this)
        happyPlaceModels = databaseHandler.getHappyPlaces()
        if (happyPlaceModels!!.size > 0) {
            for (i in happyPlaceModels!!) {
                Log.e("asuu", i.description)
            }
        }
    }
}