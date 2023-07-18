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
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.omaysoftware.omaysmart.activity.AnasayfaActivity
import com.omaysoftware.omaysmart.databinding.FragmentGirisBinding

class GirisFragment : Fragment() {

    private val ekranSuresi: Int = 2000

    private var rootBinding: FragmentGirisBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentGirisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            val action = GirisFragmentDirections.actionGirisFragmentToIzinlerFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            izinlerVar()
        }
    }

    @Suppress("DEPRECATION")
    private fun izinlerVar() {
        Handler().postDelayed({
            val intent = Intent(requireContext(), AnasayfaActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, ekranSuresi.toLong())
    }
}