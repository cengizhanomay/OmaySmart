package com.omaysoftware.omaysmart.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class CihazListesiAdapter(private val context: Context, private val listCihazListesi: List<String>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<CihazListesiViewHolder>(), CihazListesiViewHolder.ViewHolderCallback {

    interface AdapterCallback {
        fun onCihazListesiClick(cihazId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CihazListesiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_anasayfa, parent, false)
        return CihazListesiViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CihazListesiViewHolder, position: Int) {
        holder.setData(context, listCihazListesi[position])
    }

    override fun getItemCount(): Int {
        return listCihazListesi.size
    }

    override fun onCihazListesiClick(cihazId: String) {
        adapterCallback.onCihazListesiClick(cihazId)
    }
}