package com.zjut.wristband.util

import java.util.*


object TimeTransUtil {
    fun UtcToDate(utc: Long) = Date(utc * 1000)
    fun UtcToDateMillion(utc: Long) = Date(utc)

    fun getTodayUtc(): Long = getTodayUtcMillion() / 1000
    fun getTodayUtcMillion(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time.time
    }
}