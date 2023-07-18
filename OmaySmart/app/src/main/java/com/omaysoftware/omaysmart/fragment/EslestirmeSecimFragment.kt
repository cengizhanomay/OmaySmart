package com.omaysoftware.omaysmart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentEslestirmeSecimBinding

class EslestirmeSecimFragment : Fragment() {

    private var rootBinding: FragmentEslestirmeSecimBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentEslestirmeSecimBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent!!.hasExtra(Tanimlamalar().secilenEslestirmeExtra)) {
            when (val secilenEslestirme = activity?.intent!!.getStringExtra(Tanimlamalar().secilenEslestirmeExtra).toString()) {
                Tanimlamalar().qrKodOkutmaTur -> {
                    val action = EslestirmeSecimFragmentDirections.actionEslestirmeSecimFragmentToOtomatikEslestirmeFragment()
                    Navigation.findNavController(view).navigate(action)
                }
                Tanimlamalar().onlineMamaKabiTur -> {
                    val action = EslestirmeSecimFragmentDirections.actionEslestirmeSecimFragmentToManuelEslestirmeFragment(secilenEslestirme, "")
                    Navigation.findNavController(view).navigate(action)
                }
                Tanimlamalar().offlineMamaKabiTur -> {
                    val action = EslestirmeSecimFragmentDirections.actionEslestirmeSecimFragmentToManuelEslestirmeFragment(secilenEslestirme, "")
                    Navigation.findNavController(view).navigate(action)
                }
                else -> {
                    activity?.finish()
                }
            }
        }
    }
}