package com.omaysoftware.omaysmart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.omaysoftware.omaysmart.databinding.ActivityGirisBinding

class GirisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGirisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGirisBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}