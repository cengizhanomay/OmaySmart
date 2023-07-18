package com.omaysoftware.omaysmart.data

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.omaysoftware.omaysmart.R

class Tanimlamalar {
    val onlineMamaKabiTur: String = "OnlineMamaKabi"
    val offlineMamaKabiTur: String = "OfflineMamaKabi"
    val qrKodOkutmaTur: String = "QrKodOkutma"
    val varsayilanCihazWifiPassword: String = "TDF6RCdBaem1TJEvw77t"
    val varsayilanCihazWifiCapabilities: String = "WPA"
    val secilenEslestirmeExtra: String = "secilenEslestirme"
    val manuelEslestirmeFragment: String = "ManuelEslestirmeFragment"
    val otomatikEslestirmeFragment: String = "OtomatikEslestirmeFragment"
    val qrKodOkuFragment: String = "QrKodOkuFragment"
    val wifiAgBilgileriFragment: String = "WifiAgBilgileriFragment"
    val firebaseEmail: String = "omaysmartlogin@gmail.com"
    val firebasePassword: String = "TDF6RCdBaem1TJEvw77t"
    val firebaseUid: String = "tK6mkYBZvAPy2jlzuLSaOS6m4X93"
    val fDatabaseCollection: String = "MamaKabi"
    val fDataKeyAyarYapildi: String = "ayaryapildi"
    val fDataKeyFotografCek: String = "fotografcek"
    val fDataKeyFotografUrl: String = "fotografurl"
    val fDataKeyMamaVer: String = "mamaver"
    val fDataKeyOnlineKontrol: String = "onlinekontrol"
    val fDataKeyMamaSureleri: String = "mamasureleri"
    val fFieldKeySaat: String = "saat"
    val fFieldKeyDakika: String = "dakika"
    val fFieldKeyPorsiyon: String = "porsiyon"
    val secilenCihazExtra: String = "secilenCihazExtra"
}

fun ImageView.gorselIndir(url: String?, context: Context, placeholder: CircularProgressDrawable) {
    val options = RequestOptions().placeholder(placeholder).error(R.drawable.image_512)
    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

fun placeHolderYap(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}