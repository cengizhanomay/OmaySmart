package com.omaysoftware.omaysmart.data

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class CihazWifiListesiAdapter(private val listCihazWifiListesi: List<ScanResult>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<CihazWifiListesiViewHolder>(), CihazWifiListesiViewHolder.ViewHolderCallback {

    interface AdapterCallback {
        fun onCihazWifiListesiClick(scanResult: ScanResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CihazWifiListesiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_manuel_eslestirme, parent, false)
        return CihazWifiListesiViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CihazWifiListesiViewHolder, position: Int) {
        holder.setData(listCihazWifiListesi[position])
    }

    override fun getItemCount(): Int {
        return listCihazWifiListesi.size
    }

    override fun onCihazWifiListesiClick(scanResult: ScanResult) {
        adapterCallback.onCihazWifiListesiClick(scanResult)
    }
}