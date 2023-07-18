package com.omaysoftware.omaysmart.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentOtomatikEslestirmeBinding

class OtomatikEslestirmeFragment : Fragment() {

    private var rootBinding: FragmentOtomatikEslestirmeBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentOtomatikEslestirmeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val action = OtomatikEslestirmeFragmentDirections.actionOtomatikEslestirmeFragmentToQrKodOkuFragment(Tanimlamalar().otomatikEslestirmeFragment)
        Navigation.findNavController(view).navigate(action)
    }
}