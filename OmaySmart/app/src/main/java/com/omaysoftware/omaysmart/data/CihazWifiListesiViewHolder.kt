package com.omaysoftware.omaysmart.data

import android.net.wifi.ScanResult
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class CihazWifiListesiViewHolder(view: View, private val viewHolderCallback: ViewHolderCallback) : RecyclerView.ViewHolder(view) {

    private val linearLayoutManuelEslestirmeRow: LinearLayout = view.findViewById(R.id.linearLayoutManuelEslestirmeRow)
    private val textViewManuelEslestirmeRow: TextView = view.findViewById(R.id.textViewManuelEslestirmeRow)

    fun setData(scanResult: ScanResult) {
        @Suppress("DEPRECATION") val scanSSID: List<String> = scanResult.SSID.split("_")
        val wifiAdi = "OS ${scanSSID[1]} ${scanSSID[2]}"
        textViewManuelEslestirmeRow.text = wifiAdi

        linearLayoutManuelEslestirmeRow.setOnClickListener {
            viewHolderCallback.onCihazWifiListesiClick(scanResult)
        }
    }

    interface ViewHolderCallback {
        fun onCihazWifiListesiClick(scanResult: ScanResult)
    }
}