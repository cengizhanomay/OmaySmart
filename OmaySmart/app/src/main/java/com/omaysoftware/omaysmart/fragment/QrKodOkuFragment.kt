package com.omaysoftware.omaysmart.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentQrKodOkuBinding

class QrKodOkuFragment : Fragment() {

    private lateinit var okunanQrKod: String
    private lateinit var cihazTur: String
    private lateinit var cihazWifiAdi: String
    private lateinit var cihazId: String
    private lateinit var gelinenFragment: String

    private var rootBinding: FragmentQrKodOkuBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentQrKodOkuBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = ScanOptions()
        options.setPrompt("Lütfen cihazın QR Kodunu okutunuz")
        options.setCameraId(0)
        barcodeLauncher.launch(options)

        arguments?.let {
            gelinenFragment = QrKodOkuFragmentArgs.fromBundle(it).gelinenFragment
        }
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents == null) {
            activity.let {
                activity?.finish()
            }
        } else {
            okunanQrKod = result.contents
            if (okunanQrKod.isNotEmpty() && okunanQrKod.isNotBlank() && okunanQrKod.startsWith("OmaySmart")) {
                val cihazTurArray: List<String> = okunanQrKod.split("_")
                val cihazBilgiArray: List<String> = okunanQrKod.split("+")
                if (cihazTurArray.isNotEmpty() && cihazBilgiArray.isNotEmpty()) {
                    cihazTur = cihazTurArray[1]
                    cihazWifiAdi = cihazBilgiArray[0]
                    cihazId = cihazBilgiArray[1]

                    when (gelinenFragment) {
                        Tanimlamalar().otomatikEslestirmeFragment -> {
                            when (cihazTur) {
                                Tanimlamalar().onlineMamaKabiTur -> {
                                    val action = QrKodOkuFragmentDirections.actionQrKodOkuFragmentToEslestirmeKontrolFragment(cihazId, cihazWifiAdi, cihazTur, Tanimlamalar().qrKodOkuFragment)
                                    view?.let { Navigation.findNavController(it).navigate(action) }
                                }
                                Tanimlamalar().offlineMamaKabiTur -> {
                                    val action = QrKodOkuFragmentDirections.actionQrKodOkuFragmentToEslestirmeKontrolFragment(cihazId, cihazWifiAdi, cihazTur, Tanimlamalar().qrKodOkuFragment)
                                    view?.let { Navigation.findNavController(it).navigate(action) }
                                }
                                else -> {
                                    requireActivity().finish()
                                }
                            }
                        }
                        Tanimlamalar().manuelEslestirmeFragment -> {
                            when (cihazTur) {
                                Tanimlamalar().onlineMamaKabiTur -> {
                                    val action = QrKodOkuFragmentDirections.actionQrKodOkuFragmentToManuelEslestirmeFragment(cihazTur, cihazId)
                                    view?.let { Navigation.findNavController(it).navigate(action) }
                                }
                                Tanimlamalar().offlineMamaKabiTur -> {
                                    val action = QrKodOkuFragmentDirections.actionQrKodOkuFragmentToManuelEslestirmeFragment(cihazTur, cihazId)
                                    view?.let { Navigation.findNavController(it).navigate(action) }
                                }
                                else -> {
                                    requireActivity().finish()
                                }
                            }
                        }
                        else -> {
                            requireActivity().finish()
                        }
                    }
                }
            } else if (okunanQrKod.isNotEmpty() && okunanQrKod.isNotBlank() && okunanQrKod.startsWith("EslestirmeEkle")) {
                val cihazBilgiArray: List<String> = okunanQrKod.split("+")
                if (cihazBilgiArray.isNotEmpty()) {
                    cihazTur = cihazBilgiArray[1]
                    cihazWifiAdi = cihazBilgiArray[2]
                    cihazId = cihazBilgiArray[3]

                    val action = QrKodOkuFragmentDirections.actionQrKodOkuFragmentToEslestirmeEkleFragment(cihazId, cihazWifiAdi, cihazTur)
                    view?.let { Navigation.findNavController(it).navigate(action) }
                }
            } else {
                requireActivity().finish()
            }
        }
    }
}