package com.example.happyplacesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setOnClick()
    }

    private fun setOnClick() {
        binding?.fabActivityMainAdd?.setOnClickListener {
            val intent = Intent(this, AddPlacesActivity::class.java)
            startActivity(intent)
        }
    }
}