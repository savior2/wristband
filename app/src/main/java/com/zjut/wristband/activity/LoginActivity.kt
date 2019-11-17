package com.zjut.wristband.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband.R
import com.zjut.wristband.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mIdTextView: TextView
    private lateinit var mPasswordTextView: TextView
    private lateinit var mLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mIdTextView = findViewById(R.id.sid_edit_text)
        mPasswordTextView = findViewById(R.id.password_edit_text)
        mLoginButton = findViewById(R.id.login_button)
        mLoginButton.setOnClickListener { checkIn() }
    }

    private fun checkIn() {
        val id = mIdTextView.text.toString()
        val password = mPasswordTextView.text.toString()
        when {
            TextUtils.isEmpty(id) -> Toast.makeText(
                this@LoginActivity,
                "学号不能为空",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this@LoginActivity,
                "密码不能为空",
                Toast.LENGTH_SHORT
            ).show()
            id.length > 14 -> Toast.makeText(
                this@LoginActivity,
                "学号格式错误",
                Toast.LENGTH_SHORT
            ).show()
            !WebUtil.isNetworkConnected(this@LoginActivity) -> Toast.makeText(
                this@LoginActivity,
                "网络连接错误",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                Thread {
                    val result = WebUtil.checkIn(id, password)
                    if (result == WebUtil.SUCCESS) {
                        val sp = SharedPreUtil(this@LoginActivity, SharedPreFile.ACCOUNT)
                        sp.editor.putString(SharedPreKey.ID, id)
                        sp.editor.putString(SharedPreKey.PASSWORD, password)
                        sp.editor.putString(SharedPreKey.NAME, MemoryVar.name ?: "")
                        sp.editor.putString(SharedPreKey.SEX, MemoryVar.sex ?: "")
                        sp.editor.putString(SharedPreKey.TOKEN, MemoryVar.token ?: "")
                        sp.editor.apply()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Looper.prepare()
                        Toast.makeText(
                            this@LoginActivity,
                            "学号或密码错误",
                            Toast.LENGTH_SHORT
                        ).show()
                        Looper.loop()
                    }
                }.start()
            }
        }
    }
}
