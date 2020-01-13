package com.zjut.wristband.model

import org.litepal.crud.LitePalSupport


data class DailyHeartInfo(var rate: Int = 0, var utc: Long = 0, var status: Int = 0) :
    LitePalSupport()

data class AerobicsInfo(
    var num: Int = 0,
    var sid: String = "",
    var deviceId: String = "",
    var startUtc: Long = 0
) :
    LitePalSupport()

data class AerobicsHeartInfo(
    var rate: Int = 0,
    var utc: Long = 0,
    var num: Int = 0,
    var status: Int = 0
) :
    LitePalSupport()

data class AerobicsPositionInfo(
    var longitude: String = "",
    var latitude: String = "",
    var speed: Double = 0.0,
    var utc: Long = 0,
    var num: Int = 0,
    var status: Int = 0
) : LitePalSupport()


data class SportsHeartInfo(
    var sid: String = "",
    var macAddress: String = "",
    var rate: Int = 0,
    var utc: Long = 0,
    var status: Int = 0
) : LitePalSupport()


