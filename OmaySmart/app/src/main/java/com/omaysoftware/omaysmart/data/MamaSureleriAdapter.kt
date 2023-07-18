package com.omaysoftware.omaysmart.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class MamaSureleriAdapter(private val mamaSureleri: ArrayList<Map<String, Int>>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<MamaSureleriViewHolder>(), MamaSureleriViewHolder.ViewHolderCallback {

    interface AdapterCallback {
        fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MamaSureleriViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_mama_sureleri, parent, false)
        return MamaSureleriViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MamaSureleriViewHolder, position: Int) {
        holder.setData(mamaSureleri[position])
    }

    override fun getItemCount(): Int {
        return mamaSureleri.size
    }

    override fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>) {
        adapterCallback.onMamaSureleriClick(silinecekMamaSure)
    }
}