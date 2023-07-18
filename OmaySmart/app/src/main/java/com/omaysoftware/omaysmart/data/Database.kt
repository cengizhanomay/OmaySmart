package com.omaysoftware.omaysmart.data

import android.content.ContentValues
import android.content.Context
import android.widget.Toast

class Database {

    private val vDatabaseIsim: String = "omaysmartdatabase"
    private val vCihazlarTabloIsim: String = "cihazlar"
    private val vOnlineTabloIsim: String = "onlinemamacihazlar"
    private val vOfflineTabloIsim: String = "offlinemamacihazlar"
    private val vIdColIsim: String = "id"
    private val vCihazIdColIsim: String = "cihazid"
    private val vCihazWifiAdiColIsim: String = "cihazwifiadi"
    private val vCihazIsimColIsim: String = "cihazisim"
    private val vCihazTurColIsim: String = "cihaztur"
    private val vOnlineCihazIsim: String = "Online Mama Kabı"
    private val vOnlineCihazTur: String = "OnlineMamaKabi"
    private val vOfflineCihazIsim: String = "Offline Mama Kabı"
    private val vofflineCihazTur: String = "OfflineMamaKabi"

    @Suppress("SameParameterValue")
    private fun toastMesaj(context: Context, mesaj: String) {
        Toast.makeText(context.applicationContext, mesaj, Toast.LENGTH_LONG).show()
    }

    fun onlineMamaKaydet(context: Context, cihazId: String, cihazWifiAdi: String): Boolean {
        var onlineCihazKaydetKontrol = false
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

            var cihazlarSilKontrol = true
            val cihazlarCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vCihazlarTabloIsim", null)
            val cihazlarColumnIndex = cihazlarCursor.getColumnIndex(vCihazIdColIsim)
            while (cihazlarCursor.moveToNext()) {
                if (cihazlarCursor.getString(cihazlarColumnIndex) == cihazId) {
                    cihazlarSilKontrol = false
                    val cihazSilResult = database.delete(vCihazlarTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                    if (cihazSilResult != -1) {
                        cihazlarSilKontrol = true
                    }
                }
            }
            cihazlarCursor.close()

            if (cihazlarSilKontrol) {
                val cihazlarCV = ContentValues()
                cihazlarCV.put(vCihazIdColIsim, cihazId)
                val cihazEkleResult = database.insert(vCihazlarTabloIsim, null, cihazlarCV)
                if (cihazEkleResult != (-1).toLong()) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vOnlineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                    var onlineCihazSilKontrol = true
                    val onlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOnlineTabloIsim", null)
                    val onlineCihazlarColumIndex = onlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                    while (onlineCihazCursor.moveToNext()) {
                        if (onlineCihazCursor.getString(onlineCihazlarColumIndex) == cihazId) {
                            onlineCihazSilKontrol = false
                            val onlineCihazSilResult = database.delete(vOnlineTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (onlineCihazSilResult != -1) {
                                onlineCihazSilKontrol = true
                            }
                        }
                    }
                    onlineCihazCursor.close()

                    if (onlineCihazSilKontrol) {
                        val onlineCihazlarCV = ContentValues()
                        onlineCihazlarCV.put(vCihazIdColIsim, cihazId)
                        onlineCihazlarCV.put(vCihazWifiAdiColIsim, cihazWifiAdi)
                        onlineCihazlarCV.put(vCihazIsimColIsim, vOnlineCihazIsim)
                        onlineCihazlarCV.put(vCihazTurColIsim, vOnlineCihazTur)
                        val onlineCihazEkleResult = database.insert(vOnlineTabloIsim, null, onlineCihazlarCV)
                        if (onlineCihazEkleResult != (-1).toLong()) {
                            onlineCihazKaydetKontrol = true
                        }
                    }
                }
            }
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri kaydetme hatası. Tekrar deneyiniz.")
        }
        return onlineCihazKaydetKontrol
    }

    fun offlineMamaKaydet(context: Context, cihazId: String, cihazWifiAdi: String): Boolean {
        var offlineCihazKaydetKontrol = false
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

            var cihazlarSilKontrol = true
            val cihazlarCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vCihazlarTabloIsim", null)
            val cihazlarColumnIndex = cihazlarCursor.getColumnIndex(vCihazIdColIsim)
            while (cihazlarCursor.moveToNext()) {
                if (cihazlarCursor.getString(cihazlarColumnIndex) == cihazId) {
                    cihazlarSilKontrol = false
                    val cihazSilResult = database.delete(vCihazlarTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                    if (cihazSilResult != -1) {
                        cihazlarSilKontrol = true
                    }
                }
            }
            cihazlarCursor.close()

            if (cihazlarSilKontrol) {
                val cihazlarCV = ContentValues()
                cihazlarCV.put(vCihazIdColIsim, cihazId)
                val cihazEkleResult = database.insert(vCihazlarTabloIsim, null, cihazlarCV)
                if (cihazEkleResult != (-1).toLong()) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vOfflineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                    var offlineCihazSilKontrol = true
                    val offlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOfflineTabloIsim", null)
                    val offlineCihazlarColumIndex = offlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                    while (offlineCihazCursor.moveToNext()) {
                        if (offlineCihazCursor.getString(offlineCihazlarColumIndex) == cihazId) {
                            offlineCihazSilKontrol = false
                            val onlineCihazSilResult = database.delete(vOfflineTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (onlineCihazSilResult != -1) {
                                offlineCihazSilKontrol = true
                            }
                        }
                    }
                    offlineCihazCursor.close()

                    if (offlineCihazSilKontrol) {
                        val offlineCihazlarCV = ContentValues()
                        offlineCihazlarCV.put(vCihazIdColIsim, cihazId)
                        offlineCihazlarCV.put(vCihazWifiAdiColIsim, cihazWifiAdi)
                        offlineCihazlarCV.put(vCihazIsimColIsim, vOfflineCihazIsim)
                        offlineCihazlarCV.put(vCihazTurColIsim, vofflineCihazTur)
                        val onlineCihazEkleResult = database.insert(vOfflineTabloIsim, null, offlineCihazlarCV)
                        if (onlineCihazEkleResult != (-1).toLong()) {
                            offlineCihazKaydetKontrol = true
                        }
                    }
                }
            }
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri kaydetme hatası. Tekrar deneyiniz.")
        }
        return offlineCihazKaydetKontrol
    }

    fun cihazlariGetir(context: Context): ArrayList<String> {
        val cihazListId = ArrayList<String>()
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vCihazlarTabloIsim ORDER BY $vIdColIsim DESC", null)
            val cihazIdColumnIndex = cihazListCursor.getColumnIndex(vCihazIdColIsim)
            cihazListId.clear()
            while (cihazListCursor.moveToNext()) {
                cihazListId.add(cihazListCursor.getString(cihazIdColumnIndex))
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri listeleme hatası. Tekrar deneyiniz.")
        }
        return cihazListId
    }

    fun cihazKontrol(context: Context): Boolean {
        var cihazKontrol = false
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vCihazlarTabloIsim", null)
            while (cihazListCursor.moveToNext()) {
                cihazKontrol = true
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri kontrol hatası. Tekrar deneyiniz.")
        }
        return cihazKontrol
    }

    fun onlineCihazKontrol(context: Context, cihazId: String): Boolean {
        var onlineCihazKontrol = false
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vOnlineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vOnlineTabloIsim WHERE $vCihazIdColIsim = '$cihazId'", null)
            while (cihazListCursor.moveToNext()) {
                onlineCihazKontrol = true
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri kontrol hatası. Tekrar deneyiniz.")
        }
        return onlineCihazKontrol
    }

    fun offlineCihazKontrol(context: Context, cihazId: String): Boolean {
        var offlineCihazKontrol = false
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vOfflineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vOfflineTabloIsim WHERE $vCihazIdColIsim = '$cihazId'", null)
            while (cihazListCursor.moveToNext()) {
                offlineCihazKontrol = true
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri kontrol hatası. Tekrar deneyiniz.")
        }
        return offlineCihazKontrol
    }

    fun onlineCihazGetir(context: Context, cihazId: String): ArrayList<String> {
        val onlineCihazList = ArrayList<String>()
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vOnlineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vOnlineTabloIsim WHERE $vCihazIdColIsim = '$cihazId'", null)
            val cihazIdColumnIndex = cihazListCursor.getColumnIndex(vCihazIdColIsim)
            val cihazWifiAdiColumnIndex = cihazListCursor.getColumnIndex(vCihazWifiAdiColIsim)
            val cihazIsimColumnIndex = cihazListCursor.getColumnIndex(vCihazIsimColIsim)
            val cihazTurColumnIndex = cihazListCursor.getColumnIndex(vCihazTurColIsim)
            onlineCihazList.clear()
            while (cihazListCursor.moveToNext()) {
                onlineCihazList.add(cihazListCursor.getString(cihazIdColumnIndex))
                onlineCihazList.add(cihazListCursor.getString(cihazWifiAdiColumnIndex))
                onlineCihazList.add(cihazListCursor.getString(cihazIsimColumnIndex))
                onlineCihazList.add(cihazListCursor.getString(cihazTurColumnIndex))
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri listeleme hatası. Tekrar deneyiniz.")
        }
        return onlineCihazList
    }

    fun offlineCihazGetir(context: Context, cihazId: String): ArrayList<String> {
        val offlineCihazList = ArrayList<String>()
        try {
            val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS $vOfflineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

            val cihazListCursor = database.rawQuery("SELECT * FROM $vOfflineTabloIsim WHERE $vCihazIdColIsim = '$cihazId'", null)
            val cihazIdColumnIndex = cihazListCursor.getColumnIndex(vCihazIdColIsim)
            val cihazWifiAdiColumnIndex = cihazListCursor.getColumnIndex(vCihazWifiAdiColIsim)
            val cihazIsimColumnIndex = cihazListCursor.getColumnIndex(vCihazIsimColIsim)
            val cihazTurColumnIndex = cihazListCursor.getColumnIndex(vCihazTurColIsim)
            offlineCihazList.clear()
            while (cihazListCursor.moveToNext()) {
                offlineCihazList.add(cihazListCursor.getString(cihazIdColumnIndex))
                offlineCihazList.add(cihazListCursor.getString(cihazWifiAdiColumnIndex))
                offlineCihazList.add(cihazListCursor.getString(cihazIsimColumnIndex))
                offlineCihazList.add(cihazListCursor.getString(cihazTurColumnIndex))
            }
            cihazListCursor.close()
            database.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMesaj(context, "Veri listeleme hatası. Tekrar deneyiniz.")
        }
        return offlineCihazList
    }

    fun cihazSil(context: Context, cihazId: String, cihazTur: String): Boolean {
        var cihazSilKontrol = false

        when (cihazTur) {
            Tanimlamalar().onlineMamaKabiTur -> {
                try {
                    var cihazlarSilKontrol = true
                    var onlineCihazSilKontrol = true
                    val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

                    val cihazlarCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vCihazlarTabloIsim", null)
                    val cihazlarColumnIndex = cihazlarCursor.getColumnIndex(vCihazIdColIsim)
                    while (cihazlarCursor.moveToNext()) {
                        if (cihazlarCursor.getString(cihazlarColumnIndex) == cihazId) {
                            cihazlarSilKontrol = false
                            val cihazSilResult = database.delete(vCihazlarTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (cihazSilResult != -1) {
                                cihazlarSilKontrol = true
                            }
                        }
                    }
                    cihazlarCursor.close()

                    if (cihazlarSilKontrol) {
                        database.execSQL("CREATE TABLE IF NOT EXISTS $vOnlineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                        val onlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOnlineTabloIsim", null)
                        val onlineCihazlarColumIndex = onlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                        while (onlineCihazCursor.moveToNext()) {
                            if (onlineCihazCursor.getString(onlineCihazlarColumIndex) == cihazId) {
                                onlineCihazSilKontrol = false
                                val onlineCihazSilResult = database.delete(vOnlineTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                                if (onlineCihazSilResult != -1) {
                                    onlineCihazSilKontrol = true
                                }
                            }
                        }
                        onlineCihazCursor.close()
                    }
                    database.close()
                    cihazSilKontrol = onlineCihazSilKontrol && cihazlarSilKontrol
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    toastMesaj(context, "Veri silme hatası. Tekrar deneyiniz.")
                }
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                try {
                    var offlineCihazSilKontrol = true
                    var cihazlarSilKontrol = true
                    val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vCihazlarTabloIsim ($vIdColIsim INTEGER PRIMARY KEY, $vCihazIdColIsim VARCHAR)")

                    val cihazlarCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vCihazlarTabloIsim", null)
                    val cihazlarColumnIndex = cihazlarCursor.getColumnIndex(vCihazIdColIsim)
                    while (cihazlarCursor.moveToNext()) {
                        if (cihazlarCursor.getString(cihazlarColumnIndex) == cihazId) {
                            cihazlarSilKontrol = false
                            val cihazSilResult = database.delete(vCihazlarTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (cihazSilResult != -1) {
                                cihazlarSilKontrol = true
                            }
                        }
                    }
                    cihazlarCursor.close()

                    if (cihazlarSilKontrol) {
                        database.execSQL("CREATE TABLE IF NOT EXISTS $vOfflineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                        val offlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOfflineTabloIsim", null)
                        val offlineCihazlarColumIndex = offlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                        while (offlineCihazCursor.moveToNext()) {
                            if (offlineCihazCursor.getString(offlineCihazlarColumIndex) == cihazId) {
                                offlineCihazSilKontrol = false
                                val onlineCihazSilResult = database.delete(vOfflineTabloIsim, "$vCihazIdColIsim=?", arrayOf(cihazId))
                                if (onlineCihazSilResult != -1) {
                                    offlineCihazSilKontrol = true
                                }
                            }
                        }
                        offlineCihazCursor.close()
                    }
                    database.close()
                    cihazSilKontrol = offlineCihazSilKontrol && cihazlarSilKontrol
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    toastMesaj(context, "Veri silme hatası. Tekrar deneyiniz.")
                }
            }
            else -> {
                cihazSilKontrol = false
            }
        }

        return cihazSilKontrol
    }

    fun cihazAdiniGuncelle(context: Context, cihazId: String, cihazTur: String, yeniCihazAdi: String): Boolean {
        var cihazAdiniGuncelleKontrol = false

        when (cihazTur) {
            Tanimlamalar().onlineMamaKabiTur -> {
                try {
                    var onlineCihazGuncelleKontrol = true
                    val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vOnlineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                    val onlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOnlineTabloIsim", null)
                    val onlineCihazlarColumIndex = onlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                    while (onlineCihazCursor.moveToNext()) {
                        if (onlineCihazCursor.getString(onlineCihazlarColumIndex) == cihazId) {
                            onlineCihazGuncelleKontrol = false
                            val onlineCihazGuncelleCV = ContentValues()
                            onlineCihazGuncelleCV.put(vCihazIsimColIsim, yeniCihazAdi)
                            val onlineCihazGuncelleResult = database.update(vOnlineTabloIsim, onlineCihazGuncelleCV, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (onlineCihazGuncelleResult != -1) {
                                onlineCihazGuncelleKontrol = true
                            }
                        }
                    }
                    onlineCihazCursor.close()
                    database.close()
                    cihazAdiniGuncelleKontrol = onlineCihazGuncelleKontrol
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    toastMesaj(context, "Veri güncelleme hatası. Tekrar deneyiniz.")
                }
            }
            Tanimlamalar().offlineMamaKabiTur -> {
                try {
                    var offlineCihazGuncelleKontrol = true
                    val database = context.openOrCreateDatabase(vDatabaseIsim, Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS $vOfflineTabloIsim ($vCihazIdColIsim VARCHAR PRIMARY KEY, $vCihazWifiAdiColIsim VARCHAR, $vCihazIsimColIsim VARCHAR, $vCihazTurColIsim VARCHAR)")

                    val offlineCihazCursor = database.rawQuery("SELECT $vCihazIdColIsim FROM $vOfflineTabloIsim", null)
                    val offlineCihazlarColumIndex = offlineCihazCursor.getColumnIndex(vCihazIdColIsim)
                    while (offlineCihazCursor.moveToNext()) {
                        if (offlineCihazCursor.getString(offlineCihazlarColumIndex) == cihazId) {
                            offlineCihazGuncelleKontrol = false
                            val offlineCihazGuncelleCV = ContentValues()
                            offlineCihazGuncelleCV.put(vCihazIsimColIsim, yeniCihazAdi)
                            val onlineCihazGuncelleResult = database.update(vOfflineTabloIsim, offlineCihazGuncelleCV, "$vCihazIdColIsim=?", arrayOf(cihazId))
                            if (onlineCihazGuncelleResult != -1) {
                                offlineCihazGuncelleKontrol = true
                            }
                        }
                    }
                    offlineCihazCursor.close()
                    database.close()
                    cihazAdiniGuncelleKontrol = offlineCihazGuncelleKontrol
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    toastMesaj(context, "Veri güncelleme hatası. Tekrar deneyiniz.")
                }
            }
            else -> {
                cihazAdiniGuncelleKontrol = false
            }
        }

        return cihazAdiniGuncelleKontrol
    }
}