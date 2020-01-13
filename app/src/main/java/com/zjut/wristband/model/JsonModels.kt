package com.zjut.wristband.model

data class AerobicsJson(
    val token: String,
    val deviceId: String,
    val startTime: String,
    val position: String,
    val speed: String,
    val heartRate: String
)