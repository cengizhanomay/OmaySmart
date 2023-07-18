package com.omaysoftware.omaysmart.data

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.omaysoftware.omaysmart.R
import java.io.PipedOutputStream

class OfflineMamaSureleriAdapter(private val context: Fragment, private val mamaSureleriList: ArrayList<Map<String, Int>>, private val adapterCallback: AdapterCallback) : ArrayAdapter<Map<String, Int>>(context.requireContext(), R.layout.row_mama_sureleri, mamaSureleriList) {

    interface AdapterCallback {
        fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>)
    }

    fun onMamaSureleriClick(silinecekMamaSure: Map<String, Int>) {
        adapterCallback.onMamaSureleriClick(silinecekMamaSure)
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context.requireContext())
        val view: View = inflater.inflate(R.layout.row_mama_sureleri, null)

        val textViewMamaSaatiRow: TextView = view.findViewById(R.id.textViewMamaSaatiRow)
        val textViewMamaPorsiyonRow: TextView = view.findViewById(R.id.textViewMamaPorsiyonRow)
        val imageViewMamaSaatleriRow: ImageView = view.findViewById(R.id.imageViewMamaSaatleriRow)

        val sure = "${mamaSureleriList[position][Tanimlamalar().fFieldKeySaat].toString()} : ${mamaSureleriList[position][Tanimlamalar().fFieldKeyDakika].toString()}"
        textViewMamaSaatiRow.text = sure
        textViewMamaPorsiyonRow.text = mamaSureleriList[position][Tanimlamalar().fFieldKeyPorsiyon].toString()

        imageViewMamaSaatleriRow.setOnClickListener {
            onMamaSureleriClick(mamaSureleriList[position])
        }

        return view
    }
}