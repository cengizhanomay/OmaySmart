package com.omaysoftware.omaysmart.data

import android.net.wifi.ScanResult
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class WifiListesiViewHolder(view: View, private val viewHolderCallback: ViewHolderCallback) : RecyclerView.ViewHolder(view) {

    private val linearLayoutWifiAgBilgileriRow: LinearLayout = view.findViewById(R.id.linearLayoutWifiAgBilgileriRow)
    private val imageViewWifiAgBilgileriRow: ImageView = view.findViewById(R.id.imageViewWifiAgBilgileriRow)
    private val textViewWifiAgBilgileriRow: TextView = view.findViewById(R.id.textViewWifiAgBilgileriRow)

    @Suppress("DEPRECATION")
    fun setData(scanResult: ScanResult) {
        textViewWifiAgBilgileriRow.text = scanResult.SSID

        if (scanResult.level >= -50) {
            imageViewWifiAgBilgileriRow.setImageResource(R.drawable.wifi_level_full_black_icon)
        } else if (scanResult.level < -50 && scanResult.level >= -80) {
            imageViewWifiAgBilgileriRow.setImageResource(R.drawable.wifi_level_medium_black_icon)
        } else {
            imageViewWifiAgBilgileriRow.setImageResource(R.drawable.wifi_level_low_black_icon)
        }

        linearLayoutWifiAgBilgileriRow.setOnClickListener {
            viewHolderCallback.onWifiListesiClick(scanResult)
        }
    }

    interface ViewHolderCallback {
        fun onWifiListesiClick(scanResult: ScanResult)
    }
}