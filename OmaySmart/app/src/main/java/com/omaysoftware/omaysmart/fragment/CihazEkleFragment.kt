package com.omaysoftware.omaysmart.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omaysoftware.omaysmart.activity.CihazEkleActivity
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentCihazEkleBinding

class CihazEkleFragment : Fragment() {

    private var rootBinding: FragmentCihazEkleBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentCihazEkleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewCEFQrKod.setOnClickListener {
            val intent = Intent(requireContext(), CihazEkleActivity::class.java)
            intent.putExtra(Tanimlamalar().secilenEslestirmeExtra, Tanimlamalar().qrKodOkutmaTur)
            startActivity(intent)
        }

        binding.imageViewCEFOnlineMama.setOnClickListener {
            val intent = Intent(requireContext(), CihazEkleActivity::class.java)
            intent.putExtra(Tanimlamalar().secilenEslestirmeExtra, Tanimlamalar().onlineMamaKabiTur)
            startActivity(intent)
        }

        binding.imageViewCEFOfflineMama.setOnClickListener {
            val intent = Intent(requireContext(), CihazEkleActivity::class.java)
            intent.putExtra(Tanimlamalar().secilenEslestirmeExtra, Tanimlamalar().offlineMamaKabiTur)
            startActivity(intent)
        }
    }
}