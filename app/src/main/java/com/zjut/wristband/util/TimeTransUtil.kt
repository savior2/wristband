package com.zjut.wristband.util

import java.util.*


object TimeTransUtil {
    fun UtcToDate(utc: Long) = Date(utc * 1000)
    fun UtcToDateMillion(utc: Long) = Date(utc)

    fun getUtc(p0: Int = 0): Long = getUtcMillion(p0) / 1000
    fun getUtcMillion(p0: Int = 0): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + p0)
        return calendar.time.time
    }

    fun getUtcNow(): Long = getUtcNowMillion() / 1000
    fun getUtcNowMillion(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return calendar.time.time
    }
}