package com.omaysoftware.omaysmart.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.omaysoftware.omaysmart.activity.CihazListesiActivity
import com.omaysoftware.omaysmart.data.CihazListesiAdapter
import com.omaysoftware.omaysmart.data.Database
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentAnasayfaBinding

class AnasayfaFragment : Fragment(), CihazListesiAdapter.AdapterCallback {

    private var rootBinding: FragmentAnasayfaBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentAnasayfaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            binding.recyclerViewAnasayfa.layoutManager = LinearLayoutManager(it)
            binding.recyclerViewAnasayfa.addItemDecoration(DividerItemDecoration(it, LinearLayoutManager.VERTICAL))

            binding.swipeRefreshLayoutAnasayfa.setOnRefreshListener {
                cihazListele(it)
                binding.swipeRefreshLayoutAnasayfa.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            cihazListele(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cihazListele(context: Context) {
        binding.progressBarAnasayfa.visibility = View.VISIBLE
        if (Database().cihazKontrol(context)) {
            binding.textViewAnasayfa.visibility = View.GONE
            binding.progressBarAnasayfa.visibility = View.GONE
            binding.swipeRefreshLayoutAnasayfa.visibility = View.VISIBLE

            val adapter = CihazListesiAdapter(context, Database().cihazlariGetir(context), this)
            binding.recyclerViewAnasayfa.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            binding.textViewAnasayfa.visibility = View.VISIBLE
            binding.progressBarAnasayfa.visibility = View.GONE
            binding.swipeRefreshLayoutAnasayfa.visibility = View.GONE
        }
    }

    override fun onCihazListesiClick(cihazId: String) {
        val intent = Intent(requireContext(), CihazListesiActivity::class.java)
        intent.putExtra(Tanimlamalar().secilenCihazExtra, cihazId)
        startActivity(intent)
    }
}