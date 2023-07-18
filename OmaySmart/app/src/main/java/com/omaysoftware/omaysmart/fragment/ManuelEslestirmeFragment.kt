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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.omaysoftware.omaysmart.data.CihazWifiListesiAdapter
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentManuelEslestirmeBinding

@Suppress("DEPRECATION")
class ManuelEslestirmeFragment : Fragment(), CihazWifiListesiAdapter.AdapterCallback {

    private lateinit var cihazSecim: String
    private lateinit var cihazId: String
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private val focusSuresi: Int = 1000
    private val beklemeSuresi: Int = 2000

    private var rootBinding: FragmentManuelEslestirmeBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentManuelEslestirmeBinding.inflate(inflater, container, false)
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cihazSecim = ManuelEslestirmeFragmentArgs.fromBundle(it).cihazSecim
            cihazId = ManuelEslestirmeFragmentArgs.fromBundle(it).cihazId
        }

        if (cihazId.isNotEmpty() && cihazId.isNotBlank()) {
            binding.editTextCihazId.setText(cihazId)
        }

        when (cihazSecim) {
            Tanimlamalar().onlineMamaKabiTur -> {
                binding.textViewMEToolbarIsim.text = "Online Mama Kabı"
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                binding.textViewMEToolbarIsim.text = "Offline Mama Kabı"
            }
            else -> {
                binding.textViewMEToolbarIsim.text = "Omay Smart"
            }
        }

        context?.applicationContext?.let {
            binding.recyclerViewManuelEslestirme.layoutManager = LinearLayoutManager(it)
            binding.recyclerViewManuelEslestirme.addItemDecoration(DividerItemDecoration(it, LinearLayoutManager.VERTICAL))
            wifiManager = it.getSystemService(Context.WIFI_SERVICE) as WifiManager
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            it.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)

            controlWifiandLocation()
        }

        binding.swipeRefreshLayoutManuelEslestirme.setOnRefreshListener {
            controlWifiandLocation()
            binding.swipeRefreshLayoutManuelEslestirme.isRefreshing = false
        }

        binding.editTextCihazId.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }

        binding.swipeRefreshLayoutManuelEslestirme.setOnClickListener {
            binding.editTextCihazId.isFocusable = false
            Handler().postDelayed({
                binding.editTextCihazId.isFocusableInTouchMode = true
                binding.editTextCihazId.isFocusable = true
            }, focusSuresi.toLong())
        }

        binding.linearLayoutManuelEslestirmeUst.setOnClickListener {
            binding.editTextCihazId.isFocusable = false
            Handler().postDelayed({
                binding.editTextCihazId.isFocusableInTouchMode = true
                binding.editTextCihazId.isFocusable = true
            }, focusSuresi.toLong())
        }

        binding.linearLayoutManuelEslestirmeAlt.setOnClickListener {
            binding.editTextCihazId.isFocusable = false
            Handler().postDelayed({
                binding.editTextCihazId.isFocusableInTouchMode = true
                binding.editTextCihazId.isFocusable = true
            }, focusSuresi.toLong())
        }

        binding.imageViewManuelEslestirme.setOnClickListener {
            val action = ManuelEslestirmeFragmentDirections.actionManuelEslestirmeFragmentToQrKodOkuFragment(Tanimlamalar().manuelEslestirmeFragment)
            Navigation.findNavController(it).navigate(action)
        }

        binding.imageViewMEGeri.setOnClickListener {
            requireActivity().finish()
        }

        binding.buttonManuelEslestirmeKontrol.setOnClickListener {
            controlWifiandLocation()
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
        binding.linearLayoutManuelEslestirmeUst.visibility = View.INVISIBLE
        binding.linearLayoutManuelEslestirmeAlt.visibility = View.INVISIBLE
        binding.progressBarManuelEslestirme.visibility = View.VISIBLE
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
        binding.linearLayoutManuelEslestirmeUst.visibility = View.VISIBLE
        binding.linearLayoutManuelEslestirmeAlt.visibility = View.VISIBLE
        binding.progressBarManuelEslestirme.visibility = View.GONE
        val results = wifiManager.scanResults
        val degerler: List<ScanResult>
        when (cihazSecim) {
            Tanimlamalar().onlineMamaKabiTur -> {
                degerler = results.filter { it.SSID.startsWith("OmaySmart_OnlineMamaKabi") }
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                degerler = results.filter { it.SSID.startsWith("OmaySmart_OfflineMamaKabi") }
            }
            else -> {
                degerler = results.filter { it.SSID.startsWith("OmaySmart") }
            }
        }

        if (degerler.isNotEmpty()) {
            binding.swipeRefreshLayoutManuelEslestirme.visibility = View.VISIBLE
            binding.linearLayoutManuelEslestirmeKontrol.visibility = View.GONE
        } else {
            binding.swipeRefreshLayoutManuelEslestirme.visibility = View.INVISIBLE
            binding.linearLayoutManuelEslestirmeKontrol.visibility = View.VISIBLE
        }

        val adapter = CihazWifiListesiAdapter(degerler, this)
        binding.recyclerViewManuelEslestirme.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @Suppress("SameParameterValue")
    private fun toastMesaj(mesaj: String) {
        Toast.makeText(context?.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }

    override fun onCihazWifiListesiClick(scanResult: ScanResult) {
        if (binding.editTextCihazId.text.length == 36 && binding.editTextCihazId.text.isNotEmpty() && binding.editTextCihazId.text.isNotBlank()) {
            cihazId = binding.editTextCihazId.text.toString()
            if (scanResult.SSID.isNotEmpty() && scanResult.SSID.isNotBlank()) {
                val cihazWifiAdi = scanResult.SSID.toString()
                when (cihazSecim) {
                    Tanimlamalar().onlineMamaKabiTur -> {
                        val action = ManuelEslestirmeFragmentDirections.actionManuelEslestirmeFragmentToWifiAgBilgileriFragment(cihazId, cihazWifiAdi, cihazSecim)
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                    Tanimlamalar().offlineMamaKabiTur -> {
                        val action = ManuelEslestirmeFragmentDirections.actionManuelEslestirmeFragmentToCihazKaydetFragment("", "", cihazWifiAdi, cihazId, cihazSecim)
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                    else -> {
                        onAttach(requireContext())
                    }
                }
            }
        }
    }
}