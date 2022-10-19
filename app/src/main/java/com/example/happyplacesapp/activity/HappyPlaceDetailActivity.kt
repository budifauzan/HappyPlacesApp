package com.example.happyplacesapp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.R
import com.example.happyplacesapp.activity.MainActivity.Companion.EXTRA_PLACE_DETAILS
import com.example.happyplacesapp.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplacesapp.model.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private var binding: ActivityHappyPlaceDetailBinding? = null
    var happyPlaceModel: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setViewFromIntent()
        binding?.btnViewLocation?.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(EXTRA_PLACE_DETAILS, happyPlaceModel)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setViewFromIntent() {

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            happyPlaceModel = intent.getParcelableExtra(EXTRA_PLACE_DETAILS)
        }

        if (happyPlaceModel != null) {
            setSupportActionBar(binding?.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding?.toolbar?.title = happyPlaceModel?.title
            binding?.toolbar?.setNavigationOnClickListener {
                onBackPressed()
            }

            binding?.ivThumbnail?.setImageURI(Uri.parse(happyPlaceModel?.image))
            binding?.tvDescription?.text = happyPlaceModel?.description
            binding?.tvLocation?.text = happyPlaceModel?.location
        }
    }
}