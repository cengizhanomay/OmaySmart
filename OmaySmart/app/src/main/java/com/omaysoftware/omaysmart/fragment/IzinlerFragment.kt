package com.omaysoftware.omaysmart.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.omaysoftware.omaysmart.activity.AnasayfaActivity
import com.omaysoftware.omaysmart.databinding.FragmentIzinlerBinding

@Suppress("DEPRECATION")
class IzinlerFragment : Fragment() {

    private val ekranSuresi: Int = 1000
    private var konumIzin: Boolean = false
    private var kameraIzin: Boolean = false

    private var rootBinding: FragmentIzinlerBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentIzinlerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            binding.materialCardViewIzinlerKonum.visibility = View.GONE
            konumIzin = true
        } else {
            binding.materialCardViewIzinlerKonum.visibility = View.VISIBLE
            konumIzin = false
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.materialCardViewIzinlerKamera.visibility = View.GONE
            kameraIzin = true
        } else {
            binding.materialCardViewIzinlerKamera.visibility = View.VISIBLE
            kameraIzin = false
        }

        binding.materialCardViewIzinlerKonum.setOnClickListener {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.materialCardViewIzinlerKamera.setOnClickListener {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            binding.materialCardViewIzinlerKonum.visibility = View.GONE
            konumIzin = true
        } else {
            binding.materialCardViewIzinlerKonum.visibility = View.VISIBLE
            konumIzin = false
        }

        if (konumIzin && kameraIzin) {
            izinlerVar()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            binding.materialCardViewIzinlerKamera.visibility = View.GONE
            kameraIzin = true
        } else {
            binding.materialCardViewIzinlerKamera.visibility = View.VISIBLE
            kameraIzin = false
        }

        if (konumIzin && kameraIzin) {
            izinlerVar()
        }
    }

    private fun izinlerVar() {
        Handler().postDelayed({
            val intent = Intent(requireContext(), AnasayfaActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, ekranSuresi.toLong())
    }
}