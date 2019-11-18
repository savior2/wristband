package com.zjut.wristband.util

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


object WebUtil {
    private const val TAG = "WebUtil"
    const val DOMAIN = "http://www.justrun.com.cn"
    const val LOGIN_URI = "/api/sportsEquipment/getConnectServlet"
    const val SUCCESS = 0
    const val FAIL = 1
    const val NETWORK_ERROR = 2

    fun checkIn(id: String, password: String): Int {
        doPost(DOMAIN + LOGIN_URI, mapOf("username" to id, "password" to password)) {
            try {
                val jsonObject = JSONObject(it)
                val code = jsonObject.getString("Code")
                if (code != "0") {
                    return FAIL
                }
                MemoryVar.sid = jsonObject.getString("StudentId")
                MemoryVar.name = jsonObject.getString("StudentName")
                MemoryVar.sex = jsonObject.getString("StudentSex")
                MemoryVar.token = jsonObject.getString("Token")
            } catch (e: Exception) {
                LogUtil.e(TAG, e.toString())
                return NETWORK_ERROR
            }
        }
        return SUCCESS
    }

    inline fun doPost(url: String, body: Map<String, String>, callable: (String) -> Unit) {
        val requestBodyBuilder = FormBody.Builder()
        for ((k, v) in body) {
            requestBodyBuilder.add(k, v)
        }
        val requestBody = requestBodyBuilder.build()
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        val responseString = response.body?.string() ?: ""
        callable(responseString)
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null
    }
}