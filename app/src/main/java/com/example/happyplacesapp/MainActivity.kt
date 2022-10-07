package com.example.happyplacesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mActivity: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivity?.root)
        setOnClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
    }

    private fun setOnClick() {
        mActivity?.fabAdd?.setOnClickListener {
            val intent = Intent(this, AddPlacesActivity::class.java)
            startActivity(intent)
        }
    }
}