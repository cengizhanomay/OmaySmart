package com.omaysoftware.omaysmart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.omaysoftware.omaysmart.databinding.ActivityCihazListesiBinding

class CihazListesiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCihazListesiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCihazListesiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}