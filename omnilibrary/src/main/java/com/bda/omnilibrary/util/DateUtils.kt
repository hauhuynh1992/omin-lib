package com.bda.omnilibrary.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun longTimeString(time: Long, format: String, locale: Locale = Locale.getDefault()): String? {
        if (format.isBlank()) return null
        return try {
            SimpleDateFormat(format, locale).format(Date(time))
        } catch (e: Exception) {
            null
        }
    }


    fun convertStringToLongTime(
        timeStr: String,
        format: String,
        locale: Locale = Locale.getDefault()
    ): Long? {
        return convertStringToDate(timeStr, format, locale)!!.time
    }

    fun convertStringToDate(
        timeStr: String,
        format: String,
        locale: Locale = Locale.getDefault()
    ): Date? {
        if (timeStr.isBlank() || format.isBlank()) return null
        return try {
            SimpleDateFormat(format, locale).parse(timeStr)
        } catch (e: Exception) {
            null
        }
    }

}