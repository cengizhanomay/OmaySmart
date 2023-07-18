@file:Suppress("DEPRECATION")

package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
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
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.omaysoftware.omaysmart.R
import com.omaysoftware.omaysmart.data.*
import com.omaysoftware.omaysmart.databinding.FragmentOfflineMamaKabiBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OfflineMamaKabiFragment : Fragment(), MamaSureleriAdapter.AdapterCallback {

    private val cihazBaglanmaSuresi: Int = 3000
    private val vPassword: String = Tanimlamalar().varsayilanCihazWifiPassword
    private val vWifiCapabilities: String = Tanimlamalar().varsayilanCihazWifiCapabilities
    private lateinit var cihazTur: String
    private lateinit var cihazWifiAdi: String
    private lateinit var cihazId: String
    private lateinit var cihazIsim: String
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager

    private var rootBinding: FragmentOfflineMamaKabiBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentOfflineMamaKabiBinding.inflate(inflater, container, false)
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

        arguments?.let {
            cihazIsim = OnlineMamaKabiFragmentArgs.fromBundle(it).cihazIsim
            cihazId = OnlineMamaKabiFragmentArgs.fromBundle(it).cihazId
            cihazTur = OnlineMamaKabiFragmentArgs.fromBundle(it).cihazTur
            cihazWifiAdi = OnlineMamaKabiFragmentArgs.fromBundle(it).cihazWifiAdi
            binding.textViewOfflineMamaToolbarIsim.text = cihazIsim
        }

        binding.imageViewOfflineMamaMenu.setOnClickListener {
            showMenu(requireContext(), it)
        }

        binding.imageViewOfflineMamaEkle.setOnClickListener {
            mamaSuresiEkle()
        }

        binding.imageViewOfflineMamaGeri.setOnClickListener {
            requireActivity().finish()
        }

        binding.buttonOFMKYenidenBaglan.setOnClickListener {
            controlWifiandLocation()
        }

        context?.applicationContext?.let {

            /*var list: ArrayList<Map<String, Int>>? = null
            for (i in 0..1) {
                val mamaSureleriMap: Map<String, Int> = hashMapOf(Tanimlamalar().fFieldKeySaat to i, Tanimlamalar().fFieldKeyDakika to i+1, Tanimlamalar().fFieldKeyPorsiyon to i+2)

                if (list == null) {
                    list = arrayListOf(mamaSureleriMap)
                } else {
                    list.add(mamaSureleriMap)
                }
            }

            if (list != null) {
                showMamaSureleri(list)
            }*/

            binding.recyclerViewOfflineMamaKabi.layoutManager = LinearLayoutManager(it)
            binding.recyclerViewOfflineMamaKabi.addItemDecoration(DividerItemDecoration(it, LinearLayoutManager.VERTICAL))

            wifiManager = it.getSystemService(Context.WIFI_SERVICE) as WifiManager
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            controlWifiandLocation()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        binding.recyclerViewOfflineMamaKabi.adapter?.notifyDataSetChanged()
    }

    private fun toastMesaj(mesaj: String) {
        context?.applicationContext?.let {
            Toast.makeText(it, mesaj, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    private fun showMenu(context: Context, view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.cihazdetay_menu, popupMenu.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemEslestirme -> {
                    try {
                        val qrCodeText = "EslestirmeEkle+$cihazTur+$cihazWifiAdi+$cihazId"
                        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

                        val title = TextView(requireContext())
                        title.text = "EŞLEŞTİRME EKLE"
                        title.setBackgroundColor(Color.parseColor("#03577b"))
                        title.setPadding(36, 15, 10, 15)
                        title.gravity = Gravity.CENTER_VERTICAL
                        title.setTextColor(Color.WHITE)
                        title.textSize = 20f
                        builder.setCustomTitle(title)

                        builder.setMessage("Eşleştirmeyi eklemek istediğiniz telefondan QR Kodu okutunuz.")

                        val image = ImageView(requireContext())
                        val barcodeEncode = BarcodeEncoder()
                        val bitmap: Bitmap = barcodeEncode.encodeBitmap(qrCodeText, BarcodeFormat.QR_CODE, 500, 500)
                        image.setImageBitmap(bitmap)

                        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.setMargins(1, 1, 1, 1)
                        image.layoutParams = lp
                        val container = RelativeLayout(getContext())
                        val rlParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        container.layoutParams = rlParams
                        container.addView(image)
                        builder.setView(container)

                        builder.setPositiveButton("Tamam") { _, _ -> }
                        builder.create().show()
                    } catch (e: WriterException) {
                        e.printStackTrace()
                        toastMesaj("QR Kod oluşturulurken hata oluştu.")
                    }
                }
                R.id.itemEdit -> {
                    val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

                    val title = TextView(requireContext())
                    title.text = "CİHAZIN ADINI DÜZENLE"
                    title.setBackgroundColor(Color.parseColor("#03577b"))
                    title.setPadding(36, 15, 10, 15)
                    title.gravity = Gravity.CENTER_VERTICAL
                    title.setTextColor(Color.WHITE)
                    title.textSize = 20f
                    builder.setCustomTitle(title)

                    val input = EditText(requireContext())
                    input.hint = "Lütfen Cihazın Adını Giriniz"
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    input.setSingleLine()
                    input.setText(cihazIsim)
                    val filterArray = arrayOfNulls<InputFilter>(1)
                    filterArray[0] = InputFilter.LengthFilter(20)
                    input.filters = filterArray
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(36, 36, 36, 36)
                    input.layoutParams = lp
                    val container = RelativeLayout(getContext())
                    val rlParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    container.layoutParams = rlParams
                    container.addView(input)
                    builder.setView(container)

                    builder.setPositiveButton("Evet") { _, _ ->
                        val guncelCihazIsim = input.text.toString()
                        if (guncelCihazIsim.length > 4 && guncelCihazIsim.isNotEmpty() && guncelCihazIsim.isNotBlank()) {
                            updateDataSQLite(guncelCihazIsim)
                        } else {
                            toastMesaj("Lütfen geçerli bir isim giriniz.")
                        }
                    }
                    builder.setNegativeButton("Hayır") { _, _ -> }
                    builder.create().show()
                }
                R.id.itemDelete -> {
                    val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

                    val title = TextView(requireContext())
                    title.text = "EŞLEŞTİRMEYİ SİL"
                    title.setBackgroundColor(Color.parseColor("#03577b"))
                    title.setPadding(36, 15, 10, 15)
                    title.gravity = Gravity.CENTER_VERTICAL
                    title.setTextColor(Color.WHITE)
                    title.textSize = 20f
                    builder.setCustomTitle(title)

                    builder.setMessage("Cihazın eşleştirmesini silmek istediğinize emin misiniz ?")
                    builder.setPositiveButton("Evet") { _, _ ->
                        deleteDataSQLite()
                    }
                    builder.setNegativeButton("Hayır") { _, _ -> }
                    builder.create().show()
                }
            }
            true
        }

        popupMenu.show()
    }

    private fun deleteDataSQLite() {
        context?.let {
            if (Database().cihazSil(it, cihazId, cihazTur)) {
                toastMesaj("Cihaz eşleştirmesi başarılı bir şekilde silindi.")
                requireActivity().finish()
            }
        }
    }

    private fun updateDataSQLite(yeniCihazAdi: String) {
        context?.let {
            if (Database().cihazAdiniGuncelle(it, cihazId, cihazTur, yeniCihazAdi)) {
                toastMesaj("Cihaz ismi başarılı bir şekilde güncellendi.")
                binding.textViewOfflineMamaToolbarIsim.text = yeniCihazAdi
            }
        }
    }

    private fun showBaglantiHatasi() {
        MainScope().launch {
            binding.progressBarOfflineMamaKabi.visibility = View.INVISIBLE
            binding.relativeLayoutCihazBaglanmadi.visibility = View.VISIBLE
            binding.relativeLayoutOfflineMamaSaatler.visibility = View.INVISIBLE
        }

    }

    private fun showRefresh() {
        binding.progressBarOfflineMamaKabi.visibility = View.VISIBLE
        binding.relativeLayoutCihazBaglanmadi.visibility = View.INVISIBLE
        binding.relativeLayoutOfflineMamaSaatler.visibility = View.INVISIBLE
    }

    private fun showMamaSaatleri(listeDurum: Boolean) {
        MainScope().launch {
            if (listeDurum) {
                binding.progressBarOfflineMamaKabi.visibility = View.INVISIBLE
                binding.relativeLayoutCihazBaglanmadi.visibility = View.INVISIBLE
                binding.relativeLayoutOfflineMamaSaatler.visibility = View.VISIBLE
                binding.textViewOfflineMamaKabiBilgi.visibility = View.INVISIBLE
                binding.recyclerViewOfflineMamaKabi.visibility = View.VISIBLE
            } else {
                binding.progressBarOfflineMamaKabi.visibility = View.INVISIBLE
                binding.relativeLayoutCihazBaglanmadi.visibility = View.INVISIBLE
                binding.relativeLayoutOfflineMamaSaatler.visibility = View.VISIBLE
                binding.textViewOfflineMamaKabiBilgi.visibility = View.VISIBLE
                binding.recyclerViewOfflineMamaKabi.visibility = View.INVISIBLE
            }
        }
    }

    private fun controlWifiandLocation() {
        showRefresh()
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

    @Suppress("SameParameterValue")
    private fun connectWifi(wifiAdi: String, wifiSifresi: String, wifiCapabilities: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android10andMoreVersionsWithoutOuterInternet(wifiAdi, wifiSifresi, wifiCapabilities)
        } else {
            android9AndPreviousVersion(wifiAdi, wifiSifresi, wifiCapabilities)
        }
    }

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
                    val anasayfaUrl = "http://192.168.4.1/"
                    sendUrl(anasayfaUrl)
                }
            }, cihazBaglanmaSuresi.toLong())
        } else {
            showBaglantiHatasi()
        }
    }

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
                                val anasayfaUrl = "http://192.168.4.1/"
                                sendUrl(anasayfaUrl)
                            }
                        }, cihazBaglanmaSuresi.toLong())
                    } else {
                        showBaglantiHatasi()
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    showBaglantiHatasi()
                }
            }

            connectivityManager.requestNetwork(networkRequest, networkCallback)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun sendUrl(gonderilecekUrl: String) {
        if (gonderilecekUrl.isNotEmpty() && gonderilecekUrl.isNotBlank()) {
            wifiManager.connectionInfo.let {
                val cihazWifiSSID = "\"" + cihazWifiAdi + "\""
                if (it.ssid == cihazWifiSSID) {
                    binding.webViewOfflineMamaKabi.post {
                        binding.webViewOfflineMamaKabi.clearCache(true)
                        binding.webViewOfflineMamaKabi.settings.loadsImagesAutomatically = true
                        binding.webViewOfflineMamaKabi.settings.javaScriptEnabled = true
                        binding.webViewOfflineMamaKabi.settings.domStorageEnabled = true
                        binding.webViewOfflineMamaKabi.settings.javaScriptCanOpenWindowsAutomatically = true
                        binding.webViewOfflineMamaKabi.addJavascriptInterface(JavascriptInterface(), "OmaySmart")

                        binding.webViewOfflineMamaKabi.webViewClient = object : WebViewClient() {
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
                        binding.webViewOfflineMamaKabi.loadUrl(gonderilecekUrl)
                    }
                } else {
                    showBaglantiHatasi()
                }
            }
        } else {
            showBaglantiHatasi()
        }
    }

    private inner class JavascriptInterface {
        @android.webkit.JavascriptInterface
        fun saveDataSQLite(webViewBilgi: String) {
            Log.d("AndroidRuntime", webViewBilgi)
            if (webViewBilgi.startsWith("mamasaatleri")) {
                val sureBilgiArray: List<String> = webViewBilgi.split("+")
                val saatArray: List<String> = sureBilgiArray[1].split(",").filter { it.isNotEmpty() && it.isNotBlank() }
                val dakikaArray: List<String> = sureBilgiArray[2].split(",").filter { it.isNotEmpty() && it.isNotBlank() }
                val porsiyonArray: List<String> = sureBilgiArray[3].split(",").filter { it.isNotEmpty() && it.isNotBlank() }

                var mamaSureleriList: ArrayList<Map<String, Int>>? = null
                val uzunluk = (saatArray.size) - 1
                for (i in 0..uzunluk) {
                    val saat = saatArray[i].toInt()
                    val dakika = dakikaArray[i].toInt()
                    val porsiyon = porsiyonArray[i].toInt()
                    val mamaSureleriMap: Map<String, Int> = hashMapOf(Tanimlamalar().fFieldKeySaat to saat, Tanimlamalar().fFieldKeyDakika to dakika, Tanimlamalar().fFieldKeyPorsiyon to porsiyon)

                    if (mamaSureleriList == null) {
                        mamaSureleriList = arrayListOf(mamaSureleriMap)
                    } else {
                        mamaSureleriList.add(mamaSureleriMap)
                    }
                }

                if (mamaSureleriList != null) {
                    Log.d("AndroidRuntime", mamaSureleriList.toString())
                    showMamaSaatleri(true)
                    showMamaSureleri(mamaSureleriList)
                } else {
                    showMamaSaatleri(false)
                }
            } else if (webViewBilgi == "kayittrue") {
                toastMesaj("Mama saati başarılı bir şekilde kayıt edildi.")
                val anasayfaUrl = "http://192.168.4.1/"
                sendUrl(anasayfaUrl)
            } else if (webViewBilgi == "kayitfalse") {
                toastMesaj("Mama saati kaydedilirken bir sorun oluştu. Lütfen tekrar deneyiniz.")
            } else if (webViewBilgi == "deletetrue") {
                toastMesaj("Mama saati başarılı bir şekilde silindi.")
                val anasayfaUrl = "http://192.168.4.1/"
                sendUrl(anasayfaUrl)
            } else if (webViewBilgi == "deletefalse") {
                toastMesaj("Mama saati silinirken bir sorun oluştu. Lütfen tekrar deneyiniz.")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showMamaSureleri(mamaSureleriList: ArrayList<Map<String, Int>>) {
        //val adapter = OfflineMamaSureleriAdapter(this@OfflineMamaKabiFragment, mamaSureleriList, this@OfflineMamaKabiFragment)
        //binding.listView.adapter = adapter
        val adapter = MamaSureleriAdapter(mamaSureleriList, this)
        binding.recyclerViewOfflineMamaKabi.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun mamaSuresiEkle() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

        val title = TextView(requireContext())
        title.text = "MAMA SÜRESİ EKLE"
        title.setBackgroundColor(Color.parseColor("#03577b"))
        title.setPadding(36, 15, 10, 15)
        title.gravity = Gravity.CENTER_VERTICAL
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        builder.setCustomTitle(title)

        val viewAlerDialog = layoutInflater.inflate(R.layout.alert_dialog_mama_saati, null)
        val timePicker = viewAlerDialog.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        val numberPicker = viewAlerDialog.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 10

        builder.setView(viewAlerDialog)
        builder.setPositiveButton("Evet") { _, _ ->
            val secilenSaat = timePicker.hour
            val secilenDakika = timePicker.minute
            val secilenPorsiyon = numberPicker.value
            if (secilenSaat in 0..23) {
                if (secilenDakika in 0..59) {
                    if (secilenPorsiyon in 1..10) {
                        val kaydetUrl = "http://192.168.4.1/kaydet?saat=${secilenSaat}&dakika=${secilenDakika}&porsiyon=${secilenPorsiyon}"
                        sendUrl(kaydetUrl)
                    } else {
                        toastMesaj("Mama süresi kaydedilirken bir sorun oluştu. Lütfen tekrar deneyin.")
                    }
                } else {
                    toastMesaj("Mama süresi kaydedilirken bir sorun oluştu. Lütfen tekrar deneyin.")
                }
            } else {
                toastMesaj("Mama süresi kaydedilirken bir sorun oluştu. Lütfen tekrar deneyin.")
            }
        }
        builder.setNegativeButton("Hayır") { _, _ -> }
        builder.create().show()
    }

    @SuppressLint("SetTextI18n")
    override fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>) {
        val secilenMamaSaat = silinecekMamaSure[Tanimlamalar().fFieldKeySaat]
        val secilenMamaDakika = silinecekMamaSure[Tanimlamalar().fFieldKeyDakika]
        val secilenMamaPorsiyon = silinecekMamaSure[Tanimlamalar().fFieldKeyPorsiyon]

        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

        val title = TextView(requireContext())
        title.text = "Seçilen: $secilenMamaSaat : $secilenMamaDakika"
        title.setBackgroundColor(Color.parseColor("#03577b"))
        title.setPadding(36, 15, 10, 15)
        title.gravity = Gravity.CENTER_VERTICAL
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        builder.setCustomTitle(title)

        builder.setMessage("Mama süresini silmek istediğinize emin misiniz ?")
        builder.setPositiveButton("Evet") { _, _ ->
            val deleteUrl = "http://192.168.4.1/delete?saatdel=$secilenMamaSaat&dakikadel=$secilenMamaDakika&porsiyondel=$secilenMamaPorsiyon"
            sendUrl(deleteUrl)
        }
        builder.setNegativeButton("Hayır") { _, _ -> }
        builder.create().show()
    }
}