package com.omaysoftware.omaysmart.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.omaysoftware.omaysmart.data.Database
import com.omaysoftware.omaysmart.data.Tanimlamalar
import com.omaysoftware.omaysmart.databinding.FragmentEslestirmeEkleBinding
import io.grpc.okhttp.internal.Util
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable

class EslestirmeEkleFragment : Fragment() {

    private val mesajSuresi: Int = 2000
    private val sqlKaydetmeSuresi: Int = 3000
    private lateinit var cihazTur: String
    private lateinit var cihazId: String
    private lateinit var cihazWifiAdi: String

    private var rootBinding: FragmentEslestirmeEkleBinding? = null
    private val binding get() = rootBinding!!

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootBinding = FragmentEslestirmeEkleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            cihazTur = EslestirmeEkleFragmentArgs.fromBundle(it).cihazTur
            cihazId = EslestirmeEkleFragmentArgs.fromBundle(it).cihazId
            cihazWifiAdi = EslestirmeEkleFragmentArgs.fromBundle(it).cihazWifiAdi
        }

        applyLoopCount(binding.gifImageViewCEFCompleted.drawable)
        showSettingsGif()
        saveDataSQLite()

        binding.buttonCihazEkle.setOnClickListener {
            requireActivity().finish()
        }
    }

    @Suppress("SameParameterValue")
    private fun toastMesaj(mesaj: String) {
        Toast.makeText(context?.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }

    private fun applyLoopCount(gifDrawable: Drawable?) {
        if (gifDrawable is GifDrawable) {
            gifDrawable.loopCount = 1
        }
    }

    private fun showSettingsGif() {
        binding.gifImageViewCEFSettings.visibility = View.VISIBLE
        binding.gifImageViewCEFCompleted.visibility = View.INVISIBLE
    }

    private fun showCompletedGif() {
        binding.gifImageViewCEFSettings.visibility = View.GONE
        binding.gifImageViewCEFCompleted.visibility = View.VISIBLE
        binding.buttonCihazEkle.visibility = View.VISIBLE
    }

    private fun showGif() {
        MainScope().launch {
            showCompletedGif()
        }
    }

    @Suppress("DEPRECATION")
    private fun saveDataSQLite() {
        Handler().postDelayed({
            try {
                context?.let {
                    if (cihazTur == Tanimlamalar().onlineMamaKabiTur) {
                        val veritabaniDurumu = Database().onlineMamaKaydet(it, cihazId, cihazWifiAdi)
                        if (veritabaniDurumu) {
                            showGif()
                        } else {
                            toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                            Handler().postDelayed({
                                requireActivity().finish()
                            }, mesajSuresi.toLong())
                        }
                    } else if (cihazTur == Tanimlamalar().offlineMamaKabiTur) {
                        val veritabaniDurumu = Database().offlineMamaKaydet(it, cihazId, cihazWifiAdi)
                        if (veritabaniDurumu) {
                            showGif()
                        } else {
                            toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                            Handler().postDelayed({
                                requireActivity().finish()
                            }, mesajSuresi.toLong())
                        }
                    } else {
                        toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                        Handler().postDelayed({
                            requireActivity().finish()
                        }, mesajSuresi.toLong())
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                toastMesaj("Veri kaydetme sırasında bir hata oluştu. Lütfen tekrar deneyiniz.")
                Handler().postDelayed({
                    requireActivity().finish()
                }, mesajSuresi.toLong())
            }
        }, sqlKaydetmeSuresi.toLong())
    }
}