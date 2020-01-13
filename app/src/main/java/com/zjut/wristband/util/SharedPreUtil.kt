package com.zjut.wristband.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreUtil(context: Context, filename: String) {
    val sp: SharedPreferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor

    init {
        editor = sp.edit()
    }

    fun getString(key: String) = sp.getString(key, "")
    fun getInt(key: String) = sp.getInt(key, 0)
    fun getLong(key: String) = sp.getLong(key, 0)
    fun getFloat(key: String) = sp.getFloat(key, 0f)

}

object SharedPreFile {
    const val ACCOUNT = "account"
    const val STATUS = "status"
}

object SharedPreKey {
    const val ID = "studentId"
    const val PASSWORD = "password"
    const val NAME = "name"
    const val SEX = "sex"
    const val TOKEN = "token"
    const val MAC_ADDRESS = "mac_address"

    const val TIME = "time"
    const val STEP = "step"
}

