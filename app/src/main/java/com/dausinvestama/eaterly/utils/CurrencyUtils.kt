package com.dausinvestama.eaterly.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatToRupiah(value: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0 // Do not show fractional part
            // If the currency symbol should be "Rp" without a space, uncomment the following line
            // currency = Currency.getInstance("IDR").apply { symbol = "Rp" }
        }
        return numberFormat.format(value)
    }
}