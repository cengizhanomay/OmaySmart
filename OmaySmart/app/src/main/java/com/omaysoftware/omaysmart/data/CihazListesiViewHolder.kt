package com.omaysoftware.omaysmart.data

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class CihazListesiViewHolder(view: View, private val viewHolderCallback: ViewHolderCallback) : RecyclerView.ViewHolder(view) {

    private val linearLayoutAnasayfaOnlineMama: LinearLayout = view.findViewById(R.id.linearLayoutAnasayfaOnlineMama)
    private val textViewOnlineMamaIsim: TextView = view.findViewById(R.id.textViewOnlineMamaIsim)
    private val textViewOnlineMamaId: TextView = view.findViewById(R.id.textViewOnlineMamaId)
    private val textViewOnlineMamaWifiAdi: TextView = view.findViewById(R.id.textViewOnlineMamaWifiAdi)

    private val linearLayoutAnasayfaOfflineMama: LinearLayout = view.findViewById(R.id.linearLayoutAnasayfaOfflineMama)
    private val textViewOfflineMamaIsim: TextView = view.findViewById(R.id.textViewOfflineMamaIsim)
    private val textViewOfflineMamaId: TextView = view.findViewById(R.id.textViewOfflineMamaId)
    private val textViewOfflineMamaWifiAdi: TextView = view.findViewById(R.id.textViewOfflineMamaWifiAdi)

    private var cihazWifiAdi: String = ""
    private var cihazIsim: String = ""
    private var cihazTur: String = ""

    fun setData(context: Context, cihazId: String) {
        if (Database().onlineCihazKontrol(context, cihazId)) {
            linearLayoutAnasayfaOnlineMama.visibility = View.VISIBLE
            linearLayoutAnasayfaOfflineMama.visibility = View.INVISIBLE
            cihazWifiAdi = Database().onlineCihazGetir(context, cihazId)[1]
            cihazIsim = Database().onlineCihazGetir(context, cihazId)[2]
            cihazTur = Database().onlineCihazGetir(context, cihazId)[3]
            textViewOnlineMamaId.text = cihazId
            textViewOnlineMamaIsim.text = cihazIsim
            textViewOnlineMamaWifiAdi.text = cihazWifiAdi
        } else if (Database().offlineCihazKontrol(context, cihazId)) {
            linearLayoutAnasayfaOnlineMama.visibility = View.INVISIBLE
            linearLayoutAnasayfaOfflineMama.visibility = View.VISIBLE
            cihazWifiAdi = Database().offlineCihazGetir(context, cihazId)[1]
            cihazIsim = Database().offlineCihazGetir(context, cihazId)[2]
            cihazTur = Database().offlineCihazGetir(context, cihazId)[3]
            textViewOfflineMamaIsim.text = cihazIsim
            textViewOfflineMamaId.text = cihazId
            textViewOfflineMamaWifiAdi.text = cihazWifiAdi
        }

        linearLayoutAnasayfaOnlineMama.setOnClickListener {
            viewHolderCallback.onCihazListesiClick(cihazId)
        }

        linearLayoutAnasayfaOfflineMama.setOnClickListener {
            viewHolderCallback.onCihazListesiClick(cihazId)
        }
    }

    interface ViewHolderCallback {
        fun onCihazListesiClick(cihazId: String)
    }
}