package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentEslestirmeKontrolBinding

@Suppress("DEPRECATION")
class EslestirmeKontrolFragment : Fragment() {

    private lateinit var cihazTur: String
    private lateinit var cihazId: String
    private lateinit var cihazWifiAdi: String
    private lateinit var gelinenFragment: String
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private val beklemeSuresi: Int = 2000

    private var rootBinding: FragmentEslestirmeKontrolBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentEslestirmeKontrolBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cihazId = EslestirmeKontrolFragmentArgs.fromBundle(it).cihazId
            cihazTur = EslestirmeKontrolFragmentArgs.fromBundle(it).cihazTur
            cihazWifiAdi = EslestirmeKontrolFragmentArgs.fromBundle(it).cihazWifiAdi
            gelinenFragment = EslestirmeKontrolFragmentArgs.fromBundle(it).gelinenFragment
        }

        when (cihazTur) {
            Tanimlamalar().onlineMamaKabiTur -> {
                binding.textViewEKToolbarIsim.text = "Online Mama Kabı"
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                binding.textViewEKToolbarIsim.text = "Offline Mama Kabı"
            }
            else -> {
                binding.textViewEKToolbarIsim.text = "Omay Smart"
            }
        }

        if (gelinenFragment == Tanimlamalar().wifiAgBilgileriFragment) {
            val action = EslestirmeKontrolFragmentDirections.actionEslestirmeKontrolFragmentToQrKodOkuFragment(Tanimlamalar().otomatikEslestirmeFragment)
            view.let { Navigation.findNavController(it).navigate(action) }
        } else {
            context?.applicationContext?.let {
                wifiManager = it.getSystemService(Context.WIFI_SERVICE) as WifiManager
                locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                it.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)

                controlWifiandLocation()

                binding.buttonEslestirmeKontrol.setOnClickListener {
                    controlWifiandLocation()
                }

                binding.imageViewEslestirmeKontrolGeri.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun controlWifiandLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (locationManager.isLocationEnabled && wifiManager.wifiState == 3) {
                scanForWifi()
            } else {
                wifiManager.isWifiEnabled = true
                toastMesaj("Cihazın bulunabilmesi için lütfen WİFİ ve KONUM servislerini açıp yenileyin")
            }
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && wifiManager.wifiState == 3) {
                scanForWifi()
            } else {
                wifiManager.isWifiEnabled = true
                toastMesaj("Cihazın bulunabilmesi için lütfen WİFİ ve KONUM servislerini açıp yenileyin")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun scanForWifi() {
        binding.linearLayoutEslestirmeKontrol.visibility = View.INVISIBLE
        binding.progressBarEslestirmeKontrol.visibility = View.VISIBLE
        Handler().postDelayed({
            wifiManager.startScan()
        }, beklemeSuresi.toLong())
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            }
        }
    }

    @Suppress("LiftReturnOrAssignment")
    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun scanSuccess() {
        val results = wifiManager.scanResults
        val degerler = results.filter { it.SSID == cihazWifiAdi }
        if (degerler.size == 1) {
            when (cihazTur) {
                Tanimlamalar().onlineMamaKabiTur -> {
                    val action = EslestirmeKontrolFragmentDirections.actionEslestirmeKontrolFragmentToWifiAgBilgileriFragment(cihazId, cihazWifiAdi, cihazTur)
                    view?.let { Navigation.findNavController(it).navigate(action) }
                }
                Tanimlamalar().offlineMamaKabiTur -> {
                    val action = EslestirmeKontrolFragmentDirections.actionEslestirmeKontrolFragmentToCihazKaydetFragment("", "", cihazWifiAdi, cihazId, cihazTur)
                    view?.let { Navigation.findNavController(it).navigate(action) }
                }
                else -> {
                    requireActivity().finish()
                }
            }
        } else {
            binding.linearLayoutEslestirmeKontrol.visibility = View.VISIBLE
            binding.progressBarEslestirmeKontrol.visibility = View.INVISIBLE
        }
    }

    @Suppress("SameParameterValue")
    private fun toastMesaj(mesaj: String) {
        Toast.makeText(context?.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }
}
