package com.omaysoftware.omaysmart.data

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omaysoftware.omaysmart.R

class MamaSureleriViewHolder(view: View, private val viewHolderCallback: ViewHolderCallback) : RecyclerView.ViewHolder(view) {

    private val textViewMamaSaatiRow: TextView = view.findViewById(R.id.textViewMamaSaatiRow)
    private val textViewMamaPorsiyonRow: TextView = view.findViewById(R.id.textViewMamaPorsiyonRow)
    private val imageViewMamaSaatleriRow: ImageView = view.findViewById(R.id.imageViewMamaSaatleriRow)

    fun setData(mamaSureleri: Map<String, Int>) {
        if (mamaSureleri.isNotEmpty()) {
            val saat = if (mamaSureleri[Tanimlamalar().fFieldKeySaat]!!.toInt() == 0) {
                "00"
            } else {
                mamaSureleri[Tanimlamalar().fFieldKeySaat].toString()
            }

            val dakika = if (mamaSureleri[Tanimlamalar().fFieldKeyDakika]!!.toInt() == 0) {
                "00"
            } else {
                mamaSureleri[Tanimlamalar().fFieldKeyDakika].toString()
            }

            val sure = "$saat : $dakika"
            textViewMamaSaatiRow.text = sure
            textViewMamaPorsiyonRow.text = mamaSureleri[Tanimlamalar().fFieldKeyPorsiyon].toString()

            imageViewMamaSaatleriRow.setOnClickListener {
                viewHolderCallback.onMamaSureleriClick(mamaSureleri)
            }
        }
    }

    interface ViewHolderCallback {
        fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>)
    }
}