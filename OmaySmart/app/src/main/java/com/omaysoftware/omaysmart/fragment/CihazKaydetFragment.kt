@file:Suppress("DEPRECATION")

package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.omaysoftware.omaysmart.data.Common
import com.omaysoftware.omaysmart.data.Database
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentCihazKaydetBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.*

class CihazKaydetFragment : Fragment() {

    private val mesajSuresi: Int = 2000
    private val cihazBaglanmaSuresi: Int = 6000
    private val sqlKaydetmeSuresi: Int = 3000
    private var gonderilecekUrl: String = ""
    private var saat: String = ""
    private var dakika: String = ""
    private var saniye: String = ""
    private var haftagun: String = ""
    private var gun: String = ""
    private var ay: String = ""
    private var yil: String = ""
    private val vPassword: String = Tanimlamalar().varsayilanCihazWifiPassword
    private val vWifiCapabilities: String = Tanimlamalar().varsayilanCihazWifiCapabilities
    private lateinit var secilenWifiAgAdi: String
    private lateinit var secilenWifiAgSifre: String
    private lateinit var cihazTur: String
    private lateinit var cihazWifiAdi: String
    private lateinit var cihazId: String
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager

    private var rootBinding: FragmentCihazKaydetBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentCihazKaydetBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cihazId = CihazKaydetFragmentArgs.fromBundle(it).cihazId
            cihazTur = CihazKaydetFragmentArgs.fromBundle(it).cihazTur
            cihazWifiAdi = CihazKaydetFragmentArgs.fromBundle(it).cihazWifiAdi
            secilenWifiAgAdi = CihazKaydetFragmentArgs.fromBundle(it).secilenWifiAgAdi
            secilenWifiAgSifre = CihazKaydetFragmentArgs.fromBundle(it).secilenWifiAgSifre
        }

        applyLoopCount(binding.gifImageViewCompleted.drawable)
        showTransferGif()

        binding.buttonCihazKaydet.setOnClickListener {
            requireActivity().finish()
        }

        context?.applicationContext?.let {
            wifiManager = it.getSystemService(Context.WIFI_SERVICE) as WifiManager
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            controlWifiandLocation()
        }
    }

    private fun controlWifiandLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (locationManager.isLocationEnabled && wifiManager.wifiState == 3) {
                connectWifi(cihazWifiAdi, vPassword, vWifiCapabilities)
            } else {
                wifiManager.isWifiEnabled = true
                toastMesaj("Cihazın bulunabilmesi için lütfen WİFİ ve KONUM servislerini açın")
            }
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && wifiManager.wifiState == 3) {
                connectWifi(cihazWifiAdi, vPassword, vWifiCapabilities)
            } else {
                wifiManager.isWifiEnabled = true
                toastMesaj("Cihazın bulunabilmesi için lütfen WİFİ ve KONUM servislerini açın")
            }
        }
    }

    private fun toastMesaj(mesaj: String) {
        Toast.makeText(context?.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }

    private fun applyLoopCount(gifDrawable: Drawable?) {
        if (gifDrawable is GifDrawable) {
            gifDrawable.loopCount = 1
        }
    }

    private fun showTransferGif() {
        binding.gifImageViewTransfer.visibility = View.VISIBLE
        binding.gifImageViewSettings.visibility = View.INVISIBLE
        binding.gifImageViewCompleted.visibility = View.INVISIBLE
    }

    private fun showSettingsGif() {
        binding.gifImageViewTransfer.visibility = View.INVISIBLE
        binding.gifImageViewSettings.visibility = View.VISIBLE
        binding.gifImageViewCompleted.visibility = View.INVISIBLE
    }

    private fun showCompletedGif() {
        binding.gifImageViewTransfer.visibility = View.GONE
        binding.gifImageViewSettings.visibility = View.GONE
        binding.gifImageViewCompleted.visibility = View.VISIBLE
        binding.buttonCihazKaydet.visibility = View.VISIBLE
    }

    private fun showGif(gosterilecekGif: String) {
        MainScope().launch {
            if (gosterilecekGif == "gifImageViewSettings") {
                showSettingsGif()
            } else if (gosterilecekGif == "gifImageViewCompleted") {
                showCompletedGif()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun connectWifi(wifiAdi: String, wifiSifresi: String, wifiCapabilities: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android10andMoreVersionsWithoutOuterInternet(wifiAdi, wifiSifresi, wifiCapabilities)
        } else {
            android9AndPreviousVersion(wifiAdi, wifiSifresi, wifiCapabilities)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun android9AndPreviousVersion(wifiAdi: String, wifiSifresi: String, wifiCapabilities: String) {
        val conf = WifiConfiguration()
        conf.SSID = "\"" + wifiAdi + "\""
        conf.status = WifiConfiguration.Status.ENABLED
        conf.priority = 40

        if (Common.checkWifiType(wifiCapabilities) == "WEP") {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            if (wifiSifresi.matches(Regex("^[0-9a-fA-F]+$"))) {
                conf.wepKeys[0] = wifiSifresi
            } else {
                conf.wepKeys[0] = "\"" + wifiSifresi + "\""
            }
            conf.wepTxKeyIndex = 0
        } else if (Common.checkWifiType(wifiCapabilities) == "WPA") {
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            conf.preSharedKey = "\"" + wifiSifresi + "\""
        } else {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }

        wifiManager.addNetwork(conf)

        val list = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + wifiAdi + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                wifiManager.reconnect()
                break
            }
        }

        if (wifiManager.connectionInfo != null) {
            Handler().postDelayed({
                val cihazWifiSSID = "\"" + cihazWifiAdi + "\""
                if (wifiManager.connectionInfo.ssid == cihazWifiSSID) {
                    showGif("gifImageViewSettings")
                    sendUrl(secilenWifiAgAdi, secilenWifiAgSifre, cihazId)
                }
            }, cihazBaglanmaSuresi.toLong())
        } else {
            toastMesaj("Bağlantı hatası. Tekrar deneyiniz.")
            Handler().postDelayed({
                requireActivity().finish()
            }, mesajSuresi.toLong())
        }
    }

    @Suppress("DEPRECATION")
    private fun android10andMoreVersionsWithoutOuterInternet(wifiAdi: String, wifiSifresi: String, wifiCapabilities: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val wifiNetworkSpecifier: WifiNetworkSpecifier = if (Common.checkWifiType(wifiCapabilities) == "WEP") {
                WifiNetworkSpecifier.Builder().setSsid(wifiAdi).setWpa2Passphrase(wifiSifresi).build()
            } else if (Common.checkWifiType(wifiCapabilities) == "WPA") {
                WifiNetworkSpecifier.Builder().setSsid(wifiAdi).setWpa2Passphrase(wifiSifresi).build()
            } else {
                WifiNetworkSpecifier.Builder().setSsid(wifiAdi).build()
            }

            val networkRequest = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).setNetworkSpecifier(wifiNetworkSpecifier).build()
            val connectivityManager = context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    connectivityManager.bindProcessToNetwork(network)

                    if (wifiManager.connectionInfo != null) {
                        Handler().postDelayed({
                            val cihazWifiSSID = "\"" + cihazWifiAdi + "\""
                            if (wifiManager.connectionInfo.ssid == cihazWifiSSID) {
                                showGif("gifImageViewSettings")
                                sendUrl(secilenWifiAgAdi, secilenWifiAgSifre, cihazId)
                            }
                        }, cihazBaglanmaSuresi.toLong())
                    } else {
                        toastMesaj("Bağlantı hatası. Tekrar deneyiniz.")
                        Handler().postDelayed({
                            requireActivity().finish()
                        }, mesajSuresi.toLong())
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    toastMesaj("Bağlantı hatası. Tekrar deneyiniz.")
                    Handler().postDelayed({
                        requireActivity().finish()
                    }, mesajSuresi.toLong())
                }
            }
            connectivityManager.requestNetwork(networkRequest, networkCallback)
            val builder = NetworkRequest.Builder()
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    @Suppress("DEPRECATION", "VARIABLE_WITH_REDUNDANT_INITIALIZER")
    @SuppressLint("SetJavaScriptEnabled", "SimpleDateFormat", "JavascriptInterface")
    private fun sendUrl(wifiAdi: String, wifiSifresi: String, cihazId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tarihzaman = LocalDateTime.now()
            saat = tarihzaman.hour.toString()
            dakika = tarihzaman.minute.toString()
            saniye = tarihzaman.second.toString()
            haftagun = tarihzaman.dayOfWeek.value.toString()
            gun = tarihzaman.dayOfMonth.toString()
            ay = tarihzaman.monthValue.toString()
            yil = tarihzaman.year.toString()
        } else {
            val now = Time()
            now.setToNow()

            saat = now.hour.toString()
            dakika = now.minute.toString()
            saniye = now.second.toString()
            haftagun = if (now.weekDay == 0) {
                "7"
            } else {
                now.weekDay.toString()
            }
            gun = now.monthDay.toString()
            ay = ((now.month) + 1).toString()
            yil = now.year.toString()
        }

        if (cihazTur == Tanimlamalar().onlineMamaKabiTur) {
            gonderilecekUrl = "http://192.168.4.1/setting?ssid=${URLEncoder.encode(wifiAdi)}&pass=${URLEncoder.encode(wifiSifresi)}&deviceid=${URLEncoder.encode(cihazId)}"
        } else if (cihazTur == Tanimlamalar().offlineMamaKabiTur) {
            if (saniye != "" && dakika != "" && saat != "" && haftagun != "" && gun != "" && ay != "" && yil != "") {
                gonderilecekUrl = "http://192.168.4.1/setting?cihazid=${URLEncoder.encode(cihazId)}&rtcsaniye=$saniye&rtcdakika=$dakika&rtcsaat=$saat&rtchafta=$haftagun&rtcgun=$gun&rtcay=$ay&rtcyil=$yil"
            }
        } else {
            toastMesaj("Eşleştirme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
            Handler().postDelayed({
                activity?.onBackPressed()
            }, mesajSuresi.toLong())
        }

        if (gonderilecekUrl != "") {
            wifiManager.connectionInfo.let {
                val cihazWifiSSID = "\"" + cihazWifiAdi + "\""
                if (it.ssid == cihazWifiSSID) {
                    binding.webViewCihazKaydet.post {
                        binding.webViewCihazKaydet.clearCache(true)
                        binding.webViewCihazKaydet.settings.loadsImagesAutomatically = true
                        binding.webViewCihazKaydet.settings.javaScriptEnabled = true
                        binding.webViewCihazKaydet.settings.domStorageEnabled = true
                        binding.webViewCihazKaydet.settings.javaScriptCanOpenWindowsAutomatically = true
                        binding.webViewCihazKaydet.addJavascriptInterface(JavascriptInterface(), "OmaySmart")

                        binding.webViewCihazKaydet.webViewClient = object : WebViewClient() {
                            override fun onLoadResource(view: WebView?, url: String?) {
                                super.onLoadResource(view, url)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                            }
                        }
                        binding.webViewCihazKaydet.loadUrl(gonderilecekUrl)
                    }
                } else {
                    toastMesaj("Eşleştirme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                    Handler().postDelayed({
                        requireActivity().finish()
                    }, mesajSuresi.toLong())
                }
            }
        } else {
            toastMesaj("Eşleştirme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
            Handler().postDelayed({
                requireActivity().finish()
            }, mesajSuresi.toLong())
        }
    }

    private inner class JavascriptInterface {
        @android.webkit.JavascriptInterface
        fun saveDataSQLite(webViewKontrol: Boolean) {
            if (webViewKontrol) {
                Handler().postDelayed({
                    try {
                        context?.let {
                            if (cihazTur == Tanimlamalar().onlineMamaKabiTur) {
                                val veritabaniDurumu = Database().onlineMamaKaydet(it, cihazId, cihazWifiAdi)
                                if (veritabaniDurumu) {
                                    showGif("gifImageViewCompleted")
                                } else {
                                    toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                                    Handler().postDelayed({
                                        requireActivity().finish()
                                    }, mesajSuresi.toLong())
                                }
                            } else if (cihazTur == Tanimlamalar().offlineMamaKabiTur) {
                                val veritabaniDurumu = Database().offlineMamaKaydet(it, cihazId, cihazWifiAdi)
                                if (veritabaniDurumu) {
                                    showGif("gifImageViewCompleted")
                                } else {
                                    toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                                    Handler().postDelayed({
                                        requireActivity().finish()
                                    }, mesajSuresi.toLong())
                                }
                            } else {
                                toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                                Handler().postDelayed({
                                    requireActivity().finish()
                                }, mesajSuresi.toLong())
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                        Handler().postDelayed({
                            requireActivity().finish()
                        }, mesajSuresi.toLong())
                    }
                }, sqlKaydetmeSuresi.toLong())
            } else {
                toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                Handler().postDelayed({
                    requireActivity().finish()
                }, mesajSuresi.toLong())
            }
        }
    }
}