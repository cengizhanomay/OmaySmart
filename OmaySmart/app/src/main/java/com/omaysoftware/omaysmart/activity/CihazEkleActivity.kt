package com.omaysoftware.omaysmart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.omaysoftware.omaysmart.databinding.ActivityCihazEkleBinding

class CihazEkleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCihazEkleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCihazEkleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}