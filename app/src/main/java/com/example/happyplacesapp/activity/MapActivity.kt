package com.example.happyplacesapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.R
import com.example.happyplacesapp.databinding.ActivityMapBinding
import com.example.happyplacesapp.model.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var binding: ActivityMapBinding? = null
    private var mHappyPlace: HappyPlaceModel? = null
    private var supportMapFragment: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setToolbar()
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlace = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        if (mHappyPlace != null) {
            supportActionBar?.title = mHappyPlace?.title
            showLocation()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showLocation() {
        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        supportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(mHappyPlace!!.latitude, mHappyPlace!!.longitude)
        googleMap.addMarker(MarkerOptions().position(position).title(mHappyPlace?.location))
        val newLatLongZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLongZoom)
    }
}