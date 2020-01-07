package com.zjut.wristband.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lifesense.ble.LsBleManager
import com.zjut.wristband.R
import com.zjut.wristband.activity.*
import com.zjut.wristband.util.MemoryVar
import com.zjut.wristband.util.SharedPreFile
import com.zjut.wristband.util.SharedPreUtil
import com.zjut.wristband.util.WebUtil


class NavigationFragment : Fragment() {

    private lateinit var mSidTextView: TextView
    private lateinit var mNameTextView: TextView
    private lateinit var mSexTextView: TextView

    private lateinit var mDeviceManageLayout: LinearLayout
    private lateinit var mPasswordModifyLinearLayout: LinearLayout
    private lateinit var mAboutLayout: LinearLayout
    private lateinit var mLogoutLayout: LinearLayout
    private lateinit var mTestLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        mSidTextView = view.findViewById(R.id.sid_text_view)
        mNameTextView = view.findViewById(R.id.name_text_view)
        mSexTextView = view.findViewById(R.id.sex_text_view)
        mDeviceManageLayout = view.findViewById(R.id.device_manage_layout)
        mPasswordModifyLinearLayout = view.findViewById(R.id.password_modify_layout)
        mAboutLayout = view.findViewById(R.id.app_info_layout)
        mLogoutLayout = view.findViewById(R.id.logout_layout)
        mTestLayout = view.findViewById(R.id.test_layout)
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSidTextView.text = mSidTextView.text.toString() + MemoryVar.sid
        mNameTextView.text = mNameTextView.text.toString() + MemoryVar.name
        mSexTextView.text = mSexTextView.text.toString() + MemoryVar.sex
        mDeviceManageLayout.setOnClickListener {
            mDeviceManageLayout.setBackgroundColor(resources.getColor(R.color.grey))
            if (MemoryVar.device == null) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("请先连接手环！")
                builder.setCancelable(false)
                builder.setPositiveButton("确定") { _, _ ->
                    mDeviceManageLayout.setBackgroundColor(
                        resources.getColor(
                            R.color.white
                        )
                    )
                }
                builder.create().show()
            } else {
                val intent = Intent(this.activity, DeviceManageActivity::class.java)
                startActivity(intent)
            }
        }
        mPasswordModifyLinearLayout.setOnClickListener {
            mPasswordModifyLinearLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val intent = Intent(this.activity, PasswordModifyActivity::class.java)
            startActivity(intent)
        }
        mAboutLayout.setOnClickListener {
            mAboutLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val intent = Intent(this.activity, AboutActivity::class.java)
            startActivity(intent)
        }
        mLogoutLayout.setOnClickListener {
            mLogoutLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("确定退出？")
            builder.setCancelable(false)
            builder.setPositiveButton("确定") { _, _ -> logout() }
            builder.setNegativeButton("取消") { _, _ ->
                mLogoutLayout.setBackgroundColor(
                    resources.getColor(
                        R.color.white
                    )
                )
            }
            builder.create().show()
        }
        mTestLayout.setOnClickListener {
            val intent = Intent(this.activity, TestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        //有网络就断开连接
        if (WebUtil.isNetworkConnected(this@NavigationFragment.activity!!)) {
            //WebUtil.doPost(WebUtil.DOMAIN + WebUtil.LOGIN_URI, mapOf()) {}
        }

        //断开手环
        LsBleManager.getInstance().stopDataReceiveService()

        //清除用户名和密码
        val sp = SharedPreUtil(this@NavigationFragment.activity!!, SharedPreFile.ACCOUNT)
        sp.editor.clear().apply()
        //清除所有Activity
        val intent = Intent(this@NavigationFragment.activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun resetLayoutColor() {
        mDeviceManageLayout.setBackgroundColor(resources.getColor(R.color.white))
        mPasswordModifyLinearLayout.setBackgroundColor(resources.getColor(R.color.white))
        mAboutLayout.setBackgroundColor(resources.getColor(R.color.white))
        mLogoutLayout.setBackgroundColor(resources.getColor(R.color.white))
        mTestLayout.setBackgroundColor(resources.getColor(R.color.white))
    }

    override fun onResume() {
        resetLayoutColor()
        super.onResume()
    }

    companion object {
        private val TAG = "NavigationFragment"
    }
}
