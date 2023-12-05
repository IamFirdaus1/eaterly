package com.dausinvestama.eaterly.utils

import android.content.res.TypedArray
import com.dausinvestama.eaterly.data.Settings

object Utils {
    fun setSettingList(
        name: Array<String>,
        photo: TypedArray,
        desc: Array<String>
    ): ArrayList<Settings> {
        val listSettings = ArrayList<Settings>()

        for (i in name.indices) {
            val setting = Settings(name[i], photo.getResourceId(i, -1), desc[i])
            listSettings.add(setting)
        }

        return listSettings
    }
}