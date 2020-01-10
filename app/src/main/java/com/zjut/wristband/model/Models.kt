package com.zjut.wristband.model

import org.litepal.crud.LitePalSupport

data class AerobicsInfo(val sid: String, val deviceId: String)

data class DailyHeartInfo(val rate: Int, val utc: Long, val status: Int) : LitePalSupport()

data class SportsHeartInfo(
    val sid: String,
    val macAddress: String,
    val rate: Int,
    val utc: Long,
    val status: Int
) : LitePalSupport()


