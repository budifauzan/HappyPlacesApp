package com.example.happyplacesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.databinding.ActivityAddPlacesBinding
import com.google.android.material.snackbar.Snackbar

class AddPlacesActivity : AppCompatActivity() {
    private var mActivity: ActivityAddPlacesBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = ActivityAddPlacesBinding.inflate(layoutInflater)
        setContentView(mActivity?.root)
        setToolbar()
        setOnClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
    }

    private fun setToolbar() {
        setSupportActionBar(mActivity?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setOnClick() {
        mActivity?.tvAddImage?.setOnClickListener {
            Snackbar.make(mActivity!!.root, "Test", Snackbar.LENGTH_SHORT).show()
        }
    }

}