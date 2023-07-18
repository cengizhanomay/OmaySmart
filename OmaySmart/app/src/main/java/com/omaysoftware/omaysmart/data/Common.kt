package com.omaysoftware.omaysmart.data

import java.util.Locale

object Common {
    fun checkWifiType(capabilities: String): String {
        return if (capabilities.uppercase(Locale.getDefault()).contains("WEP")) {
            "WEP"
        } else if (capabilities.uppercase(Locale.getDefault()).contains("WPA")) {
            "WPA"
        } else {
            "OPEN"
        }
    }
}