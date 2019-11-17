package com.zjut.wristband.util

import android.util.Log

object LogUtil {
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private const val NOTHING = 6
    private var level = VERBOSE
    fun v(tag: String, msg: String) = if (level <= VERBOSE) Log.v(tag, msg) else Unit
    fun d(tag: String, msg: String) = if (level <= DEBUG) Log.d(tag, msg) else Unit
    fun i(tag: String, msg: String) = if (level <= INFO) Log.i(tag, msg) else Unit
    fun w(tag: String, msg: String) = if (level <= WARN) Log.w(tag, msg) else Unit
    fun e(tag: String, msg: String) = if (level <= ERROR) Log.e(tag, msg) else Unit
}