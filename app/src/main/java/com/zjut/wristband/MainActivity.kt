package com.zjut.wristband

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband.activity.HomeActivity
import com.zjut.wristband.activity.LoginActivity
import com.zjut.wristband.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAccount()
    }

    private fun checkAccount() {
        LogUtil.e(TAG, "checkAccount")
        val sp = SharedPreUtil(this@MainActivity, SharedPreFile.ACCOUNT)
        val sid = sp.sp.getString(SharedPreKey.ID, "")
        val password = sp.sp.getString(SharedPreKey.PASSWORD, "")
        //用户名密码为空或无网络连接，进入登录界面
        if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password) || !WebUtil.isNetworkConnected(
                this@MainActivity
            )
        ) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        Thread {
            val result = WebUtil.checkIn(sid!!, password!!)
            //用户名密码错误，进入登录界面
            if (result == WebUtil.FAIL) {
                startActivity(intent)
                finish()
            } else {
                //保存用户信息，并进入主界面
                sp.editor.putString(SharedPreKey.NAME, MemoryVar.name)
                sp.editor.putString(SharedPreKey.SEX, MemoryVar.sex)
                sp.editor.putString(SharedPreKey.TOKEN, MemoryVar.token)
                sp.editor.apply()
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}
