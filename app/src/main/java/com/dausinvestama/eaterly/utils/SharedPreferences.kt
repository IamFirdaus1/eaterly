package com.dausinvestama.eaterly.utils

import android.preference.PreferenceManager
import com.dausinvestama.eaterly.data.CartOrderData

class SharedPreferences(val context: android.content.Context?) {
    companion object {
        private const val  FIRST_INSTALL= "FIRST_INSTALL"
        private var  LOCATION = "LOCATION"
        private var  LOCATION_ID = null
        private var FIRST_NAME = "FIRST_NAME"
        private var EMAIL = "EMAIL"
        private var NOMOR_MEJA = "NOMOR_MEJA"
    }

    private val p = PreferenceManager.getDefaultSharedPreferences(context)

    var firstinstall = p.getBoolean(FIRST_INSTALL, false)
        set(value) = p.edit().putBoolean(FIRST_INSTALL, value).apply()

    var location = p.getString(LOCATION, "SBH")
        set(value) = p.edit().putString(LOCATION, value).apply()

    var location_id = p.getInt(LOCATION_ID, 0)
        set(value) = p.edit().putInt(LOCATION_ID, value).apply()

    var first_name = p.getString(FIRST_NAME, "Null")
        set(value) = p.edit().putString(FIRST_NAME, value).apply()

    var email = p.getString(EMAIL, "Null")
        set(value) = p.edit().putString(EMAIL, value).apply()

    var nomor_meja = p.getInt(NOMOR_MEJA, 0)
        set(value) = p.edit().putInt(NOMOR_MEJA, value).apply()



}