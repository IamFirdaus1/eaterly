package com.dausinvestama.eaterly.utils

import android.preference.PreferenceManager

class SharedPreferences(val context: android.content.Context) {
    companion object {
        private const val  FIRST_INSTALL= "FIRST_INSTALL"
    }

    private val p = PreferenceManager.getDefaultSharedPreferences(context)

    var firstinstall = p.getBoolean(FIRST_INSTALL, false)
    set(value) = p.edit().putBoolean(FIRST_INSTALL, value).apply()
}