package com.omaysoftware.omaysmart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.omaysoftware.omaysmart.databinding.ActivityAnasayfaBinding
import com.omaysoftware.omaysmart.fragment.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class AnasayfaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnasayfaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnasayfaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.animatedBottomBarAnasayfa.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                when (newIndex) {
                    0 -> {
                        loadFragment(CihazEkleFragment())
                    }
                    1 -> {
                        loadFragment(AnasayfaFragment())
                    }
                    2 -> {
                        loadFragment(BilgiFragment())
                    }
                    else -> {
                        loadFragment(AnasayfaFragment())
                    }
                }
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainerViewAnasayfa.id, fragment)
        transaction.commit()
    }
}