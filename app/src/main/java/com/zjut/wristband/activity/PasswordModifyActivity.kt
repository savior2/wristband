package com.zjut.wristband.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband.R
import com.zjut.wristband.util.*

class PasswordModifyActivity : AppCompatActivity() {

    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    private lateinit var mOldPasswordText: EditText
    private lateinit var mNewPasswordText: EditText
    private lateinit var mNewPasswordAgainText: EditText
    private lateinit var mConfirmButton: Button

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WCode.OK.num -> {
                    Toast.makeText(this@PasswordModifyActivity, "修改成功！", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PasswordModifyActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                WCode.AccountError.num -> {
                    Toast.makeText(this@PasswordModifyActivity, "密码错误！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_modify)
        initView()
    }

    private fun initView() {
        mTitleTextView = findViewById(R.id.title_text_view)
        mBackButton = findViewById(R.id.back_button)
        mOldPasswordText = findViewById(R.id.old_password_text)
        mNewPasswordText = findViewById(R.id.new_password_text)
        mNewPasswordAgainText = findViewById(R.id.new_password_again_text)
        mConfirmButton = findViewById(R.id.password_modify_button)
        mTitleTextView.text = "修改密码"
        mBackButton.visibility = View.VISIBLE
        mBackButton.setOnClickListener {
            finish()
        }
        mConfirmButton.setOnClickListener {
            val old = mOldPasswordText.text.toString()
            val new = mNewPasswordText.text.toString()
            val newAgain = mNewPasswordAgainText.text.toString()
            if (TextUtils.isEmpty(old) || TextUtils.isEmpty(new) || TextUtils.isEmpty(newAgain)) {
                Toast.makeText(this, "以上参数不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("确定修改？")
            builder.setPositiveButton("确定") { _, _ -> modifyPassword(old, new, newAgain) }
            builder.setNegativeButton("取消") { _, _ -> }
            builder.create().show()
        }
    }

    private fun modifyPassword(old: String, new: String, newAgain: String) {
        val savedOld =
            SharedPreUtil(this, SharedPreFile.ACCOUNT).getString(SharedPreKey.PASSWORD) ?: ""
        val sid = SharedPreUtil(this, SharedPreFile.ACCOUNT).getString(SharedPreKey.ID) ?: ""
        when {
            new != newAgain -> {
                Toast.makeText(this, "两次密码不一致！", Toast.LENGTH_SHORT).show()
                return
            }
            old != savedOld -> {
                Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                Thread {
                    val params = mapOf(
                        "username" to sid,
                        "oldPassword" to old,
                        "newPassword" to new,
                        "newPassword2" to newAgain
                    )
                    val result = WebUtil.modifyPassword(params)
                    val msg = mHandler.obtainMessage()
                    msg.what = result.num
                    mHandler.sendMessage(msg)
                }.start()
            }
        }
    }

    companion object {
        private val TAG = "PasswordModifyActivity"
    }
}