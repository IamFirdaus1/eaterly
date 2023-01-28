package com.dausinvestama.eaterly.utils

import android.preference.PreferenceManager
import com.dausinvestama.eaterly.data.CartOrderData

class SharedPreferences(val context: android.content.Context?) {
    companion object {
        private const val  FIRST_INSTALL= "FIRST_INSTALL"
        private var  LOCATION = "LOCATION"
        private var FIRST_NAME = "FIRST_NAME"
        var ARRAYCART: ArrayList<CartOrderData> = ArrayList()
    }

    private val p = PreferenceManager.getDefaultSharedPreferences(context)

    var firstinstall = p.getBoolean(FIRST_INSTALL, false)
    set(value) = p.edit().putBoolean(FIRST_INSTALL, value).apply()

    var location = p.getString(LOCATION, "SBH")
    set(value) = p.edit().putString(LOCATION, value).apply()

    var first_name = p.getString(FIRST_NAME, "Null")
        set(value) = p.edit().putString(FIRST_NAME, value).apply()

}