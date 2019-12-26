package com.zjut.wristband.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreUtil(val context: Context, val filename: String) {
    val sp: SharedPreferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor

    init {
        editor = sp.edit()
    }
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

    const val TIME = "time"
    const val STEP = "step"
}

