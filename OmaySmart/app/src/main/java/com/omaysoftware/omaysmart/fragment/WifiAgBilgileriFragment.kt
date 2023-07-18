package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.data.WifiListesiAdapter
import com.omaysoftware.omaysmart.databinding.FragmentWifiAgBilgileriBinding

@Suppress("DEPRECATION")
class WifiAgBilgileriFragment : Fragment(), WifiListesiAdapter.AdapterCallback {

    private lateinit var cihazTur: String
    private lateinit var cihazId: String
    private lateinit var cihazWifiAdi: String
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private val focusSuresi: Int = 1000
    private val beklemeSuresi: Int = 2000

    private var rootBinding: FragmentWifiAgBilgileriBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentWifiAgBilgileriBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = WifiAgBilgileriFragmentDirections.actionWifiAgBilgileriFragmentToEslestirmeKontrolFragment(cihazId, cihazWifiAdi, cihazTur, Tanimlamalar().wifiAgBilgileriFragment)
                view?.let { Navigation.findNavController(it).navigate(action) }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cihazTur = WifiAgBilgileriFragmentArgs.fromBundle(it).cihazTur
            cihazId = WifiAgBilgileriFragmentArgs.fromBundle(it).cihazId
            cihazWifiAdi = WifiAgBilgileriFragmentArgs.fromBundle(it).cihazWifiAdi
        }

        when (cihazTur) {
            Tanimlamalar().onlineMamaKabiTur -> {
                binding.textViewWABToolbarIsim.text = "Online Mama Kabı"
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                binding.textViewWABToolbarIsim.text = "Offline Mama Kabı"
            }
            else -> {
                binding.textViewWABToolbarIsim.text = "Omay Smart"
            }
        }

        context?.applicationContext?.let {
            binding.recyclerViewWifiAgBilgileri.layoutManager = LinearLayoutManager(it)
            binding.recyclerViewWifiAgBilgileri.addItemDecoration(DividerItemDecoration(it, LinearLayoutManager.VERTICAL))
            wifiManager = it.getSystemService(Context.WIFI_SERVICE) as WifiManager
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            it.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)

            controlWifiandLocation()
        }

        binding.swipeRefreshLayoutWifiAgBilgileri.setOnRefreshListener {
            controlWifiandLocation()
            binding.swipeRefreshLayoutWifiAgBilgileri.isRefreshing = false
        }

        binding.editTextWifiPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }

        binding.linearLayoutWifiList.setOnClickListener {
            binding.editTextWifiPassword.isFocusable = false
            Handler().postDelayed({
                binding.editTextWifiPassword.isFocusableInTouchMode = true
                binding.editTextWifiPassword.isFocusable = true
            }, focusSuresi.toLong())
        }

        binding.linearLayoutWifiBilgileri.setOnClickListener {
            binding.editTextWifiPassword.isFocusable = false
            Handler().postDelayed({
                binding.editTextWifiPassword.isFocusableInTouchMode = true
                binding.editTextWifiPassword.isFocusable = true
            }, focusSuresi.toLong())
        }

        binding.buttonWifiAgBilgileriIleri.setOnClickListener {
            if (binding.editTextWifiPassword.text.length >= 8 && binding.editTextWifiPassword.text.isNotEmpty() && binding.editTextWifiPassword.text.isNotBlank() && binding.editTextWifiSSID.text.isNotEmpty() && binding.editTextWifiSSID.text.isNotBlank()) {
                val secilenWifiAgAdi = binding.editTextWifiSSID.text.toString()
                val secilenWifiAgSifre = binding.editTextWifiPassword.text.toString()

                val action = WifiAgBilgileriFragmentDirections.actionWifiAgBilgileriFragmentToCihazKaydetFragment(secilenWifiAgAdi, secilenWifiAgSifre, cihazWifiAdi, cihazId, cihazTur)
                Navigation.findNavController(it).navigate(action)
            }
        }

        binding.imageViewWABGeri.setOnClickListener {
            val action = WifiAgBilgileriFragmentDirections.actionWifiAgBilgileriFragmentToEslestirmeKontrolFragment(cihazId, cihazWifiAdi, cihazTur, Tanimlamalar().wifiAgBilgileriFragment)
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
        binding.linearLayoutWifiList.visibility = View.INVISIBLE
        binding.linearLayoutWifiBilgileri.visibility = View.INVISIBLE
        binding.progressBarWifiAgBilgileri.visibility = View.VISIBLE
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
        binding.linearLayoutWifiList.visibility = View.VISIBLE
        binding.linearLayoutWifiBilgileri.visibility = View.VISIBLE
        binding.progressBarWifiAgBilgileri.visibility = View.GONE
        val results = wifiManager.scanResults
        val degerler = results.filter { !it.SSID.startsWith("OmaySmart") }
        val adapter = WifiListesiAdapter(degerler, this)
        binding.recyclerViewWifiAgBilgileri.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @Suppress("SameParameterValue")
    private fun toastMesaj(mesaj: String) {
        Toast.makeText(context?.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }

    override fun onWifiListesiClick(scanResult: ScanResult) {
        if (scanResult.SSID.isNotEmpty() && scanResult.SSID.isNotBlank()) {
            binding.editTextWifiSSID.isActivated = true
            binding.editTextWifiSSID.setText(scanResult.SSID.toString())
        }
    }
}