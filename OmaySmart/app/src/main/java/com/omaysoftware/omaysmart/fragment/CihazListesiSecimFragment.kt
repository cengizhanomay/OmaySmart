package com.omaysoftware.omaysmart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.omaysoftware.omaysmart.data.Database
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentCihazListesiSecimBinding

class CihazListesiSecimFragment : Fragment() {

    private lateinit var cihazId: String
    private lateinit var cihazWifiAdi: String
    private lateinit var cihazIsim: String
    private lateinit var cihazTur: String

    private var rootBinding: FragmentCihazListesiSecimBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentCihazListesiSecimBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.applicationContext?.let { itContext ->
            if (activity?.intent!!.hasExtra(Tanimlamalar().secilenCihazExtra)) {
                cihazId = activity?.intent!!.getStringExtra(Tanimlamalar().secilenCihazExtra).toString()

                if (Database().onlineCihazKontrol(itContext, cihazId)) {
                    cihazWifiAdi = Database().onlineCihazGetir(itContext, cihazId)[1]
                    cihazIsim = Database().onlineCihazGetir(itContext, cihazId)[2]
                    cihazTur = Database().onlineCihazGetir(itContext, cihazId)[3]

                    val action = CihazListesiSecimFragmentDirections.actionCihazListesiFragmentToOnlineMamaKabiFragment(cihazId, cihazIsim, cihazWifiAdi, cihazTur)
                    Navigation.findNavController(view).navigate(action)
                } else if (Database().offlineCihazKontrol(itContext, cihazId)) {
                    cihazWifiAdi = Database().offlineCihazGetir(itContext, cihazId)[1]
                    cihazIsim = Database().offlineCihazGetir(itContext, cihazId)[2]
                    cihazTur = Database().offlineCihazGetir(itContext, cihazId)[3]

                    val action = CihazListesiSecimFragmentDirections.actionCihazListesiFragmentToOfflineMamaKabiFragment(cihazId, cihazIsim, cihazWifiAdi, cihazTur)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
    }
}