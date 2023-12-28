package com.dausinvestama.eaterly.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    fun timestampStringToDate(timestampString: String, pattern: String = "dd-MM-yyyy HH:mm", timeZone: TimeZone = TimeZone.getDefault()): String? {
        // Extract the seconds part from the string
        val regex = "Timestamp\\(seconds=(\\d+),".toRegex()
        val matchResult = regex.find(timestampString)

        // Parse the seconds as a Long and create a Date object
        val seconds = matchResult?.groups?.get(1)?.value?.toLongOrNull() ?: return null
        val date = Date(seconds * 1000) // Convert seconds to milliseconds

        // Use SimpleDateFormat to get the date and time in the desired format
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.timeZone = timeZone

        return simpleDateFormat.format(date)
    }
}