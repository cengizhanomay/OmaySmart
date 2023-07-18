package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.omaysoftware.omaysmart.R
import com.omaysoftware.omaysmart.data.*
import com.omaysoftware.omaysmart.databinding.FragmentOnlineMamaKabiBinding

class OnlineMamaKabiFragment : Fragment(), MamaSureleriAdapter.AdapterCallback {

    private lateinit var cihazTur: String
    private lateinit var cihazId: String
    private lateinit var cihazWifiAdi: String
    private lateinit var cihazIsim: String
    private val resimSuresi: Int = 15000
    private val mamaSuresi: Int = 5000
    private lateinit var auth: FirebaseAuth
    private var kullaniciKontrol: FirebaseUser? = null
    private lateinit var database: FirebaseFirestore
    private var onlineKontrol: Boolean? = null
    private var mamaSureleri: ArrayList<Map<String, Int>>? = null

    private var rootBinding: FragmentOnlineMamaKabiBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentOnlineMamaKabiBinding.inflate(inflater, container, false)
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
            binding.textViewOnlineMamaToolbarIsim.text = cihazIsim
        }

        binding.imageViewOnlineMamaMenu.setOnClickListener {
            showMenu(requireContext(), it)
        }

        context?.applicationContext?.let { itContext ->
            binding.recyclerViewOnlineMamaKabi.layoutManager = LinearLayoutManager(itContext)
            binding.recyclerViewOnlineMamaKabi.addItemDecoration(DividerItemDecoration(itContext, LinearLayoutManager.VERTICAL))
            auth = FirebaseAuth.getInstance()
            database = FirebaseFirestore.getInstance()
            binding.imageViewOnlineMamaGeri.setOnClickListener {
                requireActivity().finish()
            }

            if (checkForInternet(itContext)) {
                kullaniciKontrol = auth.currentUser
                if (kullaniciKontrol == null) {
                    auth.signInWithEmailAndPassword(Tanimlamalar().firebaseEmail, Tanimlamalar().firebasePassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            kullaniciKontrol = auth.currentUser
                            kullaniciKontrol?.let { baglananKullanici ->
                                if (baglananKullanici.uid == Tanimlamalar().firebaseUid) {
                                    firebaseGetData(itContext)
                                    binding.imageViewOnlineMamaRefresh.setOnClickListener {
                                        firebaseSetGetPhoto()
                                    }
                                    binding.imageViewOnlineMamaEkle.setOnClickListener {
                                        mamaSuresiEkle()
                                    }
                                    binding.buttonOnlineMamaKabi.setOnClickListener {
                                        mamaVer()
                                    }
                                }
                            }
                        }
                    }.addOnFailureListener {
                        binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
                        toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                    }
                } else {
                    kullaniciKontrol?.let { baglananKullanici ->
                        if (baglananKullanici.uid == Tanimlamalar().firebaseUid) {
                            firebaseGetData(itContext)
                            binding.imageViewOnlineMamaRefresh.setOnClickListener {
                                firebaseSetGetPhoto()
                            }
                            binding.imageViewOnlineMamaEkle.setOnClickListener {
                                mamaSuresiEkle()
                            }
                            binding.buttonOnlineMamaKabi.setOnClickListener {
                                mamaVer()
                            }
                        }
                    }
                }
            } else {
                binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
                toastMesaj("İnternet bağlantısı bulunamadı. Lütfen internete bağlanıp tekrar deneyin.")
            }
        }
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
                    filterArray[0] = LengthFilter(20)
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

    @SuppressLint("ObsoleteSdkInt")
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }
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
                binding.textViewOnlineMamaToolbarIsim.text = yeniCihazAdi
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNCHECKED_CAST")
    private fun firebaseGetData(context: Context) {
        val onlineKontrolData = hashMapOf(Tanimlamalar().fDataKeyOnlineKontrol to true, Tanimlamalar().fDataKeyAyarYapildi to true)
        database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(onlineKontrolData as Map<String, Any>).addOnSuccessListener {}.addOnFailureListener {
            binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
            toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
        }

        database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).addSnapshotListener { value, error ->
            if (error != null) {
                binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
                toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
            } else {
                if (value != null) {
                    val valdata = value.data
                    if (valdata != null) {
                        if (valdata[Tanimlamalar().fDataKeyOnlineKontrol] != null) {
                            onlineKontrol = valdata[Tanimlamalar().fDataKeyOnlineKontrol] as Boolean
                            onlineKontrol.let {
                                if (!onlineKontrol!!) {
                                    binding.viewOnlineMamaKabi.setBackgroundColor(Color.parseColor("#008000"))
                                } else {
                                    binding.viewOnlineMamaKabi.setBackgroundColor(Color.parseColor("#ff0000"))
                                }
                            }
                        }

                        if (valdata[Tanimlamalar().fDataKeyMamaSureleri] != null) {
                            if (valdata[Tanimlamalar().fDataKeyMamaSureleri].toString().isNotEmpty()) {
                                binding.textViewOnlineMamaKabiBilgi.visibility = View.GONE
                                binding.recyclerViewOnlineMamaKabi.visibility = View.VISIBLE
                                mamaSureleri = valdata[Tanimlamalar().fDataKeyMamaSureleri] as ArrayList<Map<String, Int>>
                                mamaSureleri.let {
                                    val adapter = MamaSureleriAdapter(mamaSureleri!!, this)
                                    binding.recyclerViewOnlineMamaKabi.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                }
                            } else {
                                mamaSureleri = null
                                binding.textViewOnlineMamaKabiBilgi.visibility = View.VISIBLE
                                binding.recyclerViewOnlineMamaKabi.visibility = View.INVISIBLE
                            }
                        } else {
                            mamaSureleri = null
                            binding.textViewOnlineMamaKabiBilgi.visibility = View.VISIBLE
                            binding.recyclerViewOnlineMamaKabi.visibility = View.INVISIBLE
                        }

                        if (valdata[Tanimlamalar().fDataKeyFotografUrl] != null) {
                            val fotografUrl: String = valdata[Tanimlamalar().fDataKeyFotografUrl].toString()
                            if (fotografUrl.isNotEmpty() && fotografUrl.isNotBlank()) {
                                if (fotografUrl != "null") {
                                    if (fotografUrl != "false") {
                                        binding.imageViewOnlineMamaKabi.gorselIndir(fotografUrl, context, placeHolderYap(context))
                                    } else {
                                        binding.imageViewOnlineMamaKabi.setImageResource(R.drawable.image_512)
                                        toastMesaj("Fotoğraf alınırken bir sorun oluştu. Lütfen tekrar deneyin.")
                                    }
                                }
                            }
                        }
                    } else {
                        binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
                        toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                    }
                } else {
                    binding.relativeLayoutOnlineMamaKabi.visibility = View.GONE
                    toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                }
            }
        }
    }

    private fun firebaseSetGetPhoto() {
        onlineKontrol.let {
            if (!onlineKontrol!!) {
                binding.imageViewOnlineMamaRefresh.visibility = View.INVISIBLE
                toastMesaj("Resmin yüklenmesi 10-15 saniye sürebilir.")
                @Suppress("DEPRECATION") Handler().postDelayed({
                    binding.imageViewOnlineMamaRefresh.visibility = View.VISIBLE
                }, resimSuresi.toLong())
                val fotografData = hashMapOf(Tanimlamalar().fDataKeyFotografCek to true, Tanimlamalar().fDataKeyAyarYapildi to true)
                database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(fotografData as Map<String, Any>).addOnSuccessListener {}.addOnFailureListener {
                    binding.imageViewOnlineMamaKabi.setImageResource(R.drawable.image_512)
                    toastMesaj("Fotoğraf alınırken bir sorun oluştu. Lütfen tekrar deneyin.")
                }
            } else {
                toastMesaj("Cihaz online değil. Lütfen tekrar deneyiniz.")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>) {
        val saat = if (silinecekMamaSure[Tanimlamalar().fFieldKeySaat]!!.toInt() == 0) {
            "00"
        } else {
            silinecekMamaSure[Tanimlamalar().fFieldKeySaat].toString()
        }

        val dakika = if (silinecekMamaSure[Tanimlamalar().fFieldKeyDakika]!!.toInt() == 0) {
            "00"
        } else {
            silinecekMamaSure[Tanimlamalar().fFieldKeyDakika].toString()
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

        val title = TextView(requireContext())
        title.text = "Seçilen: $saat : $dakika"
        title.setBackgroundColor(Color.parseColor("#03577b"))
        title.setPadding(36, 15, 10, 15)
        title.gravity = Gravity.CENTER_VERTICAL
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        builder.setCustomTitle(title)

        builder.setMessage("Mama süresini silmek istediğinize emin misiniz ?")
        builder.setPositiveButton("Evet") { _, _ ->
            database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(Tanimlamalar().fDataKeyMamaSureleri, FieldValue.arrayRemove(silinecekMamaSure)).addOnSuccessListener {
                toastMesaj("Mama süresi başarılı bir şekilde silindi.")
                val ayarYapildiData = hashMapOf(Tanimlamalar().fDataKeyAyarYapildi to true)
                database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(ayarYapildiData as Map<String, Any>)
            }.addOnFailureListener {
                toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
            }
        }
        builder.setNegativeButton("Hayır") { _, _ -> }
        builder.create().show()
    }

    private fun mamaVer() {
        onlineKontrol.let {
            if (!onlineKontrol!!) {
                val mamaVerData = hashMapOf(Tanimlamalar().fDataKeyMamaVer to true)
                database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(mamaVerData as Map<String, Any>).addOnSuccessListener {
                    toastMesaj("Mama verme işlemi başarılı.")
                    binding.buttonOnlineMamaKabi.isEnabled = false
                    @Suppress("DEPRECATION") Handler().postDelayed({
                        binding.buttonOnlineMamaKabi.isEnabled = true
                        val mamaVermeData = hashMapOf(Tanimlamalar().fDataKeyMamaVer to false)
                        database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(mamaVermeData as Map<String, Any>)
                    }, mamaSuresi.toLong())
                }.addOnFailureListener {
                    toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                }
            } else {
                toastMesaj("Cihaz online değil. Lütfen tekrar deneyiniz.")
            }
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
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
                        val secilenMamaSureleriMap: Map<String, Int> = hashMapOf(Tanimlamalar().fFieldKeyDakika to secilenDakika, Tanimlamalar().fFieldKeyPorsiyon to secilenPorsiyon, Tanimlamalar().fFieldKeySaat to secilenSaat)
                        var mamaSureleriList: List<Map<String, Int>>? = null

                        if (!mamaSureleri.isNullOrEmpty()) {
                            mamaSureleri?.let { itMamaSureleri ->
                                mamaSureleriList = if (itMamaSureleri.size > 0) {
                                    itMamaSureleri.add(secilenMamaSureleriMap)
                                    itMamaSureleri
                                } else {
                                    listOf(secilenMamaSureleriMap)
                                }
                            }
                        } else {
                            mamaSureleriList = listOf(secilenMamaSureleriMap)
                        }

                        if (!mamaSureleriList.isNullOrEmpty()) {
                            val mamaSureleriData = hashMapOf(Tanimlamalar().fDataKeyMamaSureleri to mamaSureleriList)
                            database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).set(mamaSureleriData, SetOptions.merge()).addOnSuccessListener {
                                toastMesaj("Mama süresi başarılı bir şekilde eklendi.")
                                val ayarYapildiData = hashMapOf(Tanimlamalar().fDataKeyAyarYapildi to true)
                                database.collection(Tanimlamalar().fDatabaseCollection).document(cihazId).update(ayarYapildiData as Map<String, Any>)
                            }.addOnFailureListener {
                                toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                            }
                        } else {
                            toastMesaj("Veri tabanına bağlanırken bir sorun oluştu. Lütfen tekrar deneyin.")
                        }
                    }
                }
            }
        }
        builder.setNegativeButton("Hayır") { _, _ -> }
        builder.create().show()
    }
}