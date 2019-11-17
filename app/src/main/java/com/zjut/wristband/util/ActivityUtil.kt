package com.zjut.wristband.util

import android.app.Activity

object ActivityUtil {
    private val activities = ArrayList<Activity>()
    fun addActivity(activity: Activity) = activities.add(activity)
    fun removeActivity(activity: Activity) = activities.remove(activity)
    fun finishAll() {
        for (activity in activities) {
            activity.finish()
        }
    }
}