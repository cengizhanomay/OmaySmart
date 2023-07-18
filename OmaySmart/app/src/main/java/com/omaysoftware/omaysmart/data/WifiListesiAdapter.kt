package com.omaysoftware.omaysmart.data

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class WifiListesiAdapter(private val listWifiListesi: List<ScanResult>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<WifiListesiViewHolder>(), WifiListesiViewHolder.ViewHolderCallback {

    interface AdapterCallback {
        fun onWifiListesiClick(scanResult: ScanResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiListesiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_wifi_ag_bilgileri, parent, false)
        return WifiListesiViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: WifiListesiViewHolder, position: Int) {
        holder.setData(listWifiListesi[position])
    }

    override fun getItemCount(): Int {
        return listWifiListesi.size
    }

    override fun onWifiListesiClick(scanResult: ScanResult) {
        adapterCallback.onWifiListesiClick(scanResult)
    }
}