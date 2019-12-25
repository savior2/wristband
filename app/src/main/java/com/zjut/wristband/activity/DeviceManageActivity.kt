package com.zjut.wristband.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.OnSettingListener
import com.lifesense.ble.bean.constant.HeartRateDetectionMode
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar

class DeviceManageActivity : AppCompatActivity() {

    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    private lateinit var mDeviceNameTextView: TextView
    private lateinit var mDeviceStateTextView: TextView

    private lateinit var mFunDisconnectLayout: RelativeLayout
    private lateinit var mFunHrDetectModeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_manage)
        initView()
        initComponent()
    }


    private fun initView() {
        mTitleTextView = findViewById(R.id.title_text_view)
        mTitleTextView.text = "手环管理"
        mBackButton = findViewById(R.id.back_button)
        mBackButton.setOnClickListener { this.finish() }
        mBackButton.visibility = View.VISIBLE
        mDeviceNameTextView = findViewById(R.id.device_name_text_view)
        mDeviceNameTextView.text =
            MemoryVar.device?.deviceName + " [" + MemoryVar.device?.macAddress + "]"
        mDeviceStateTextView = findViewById(R.id.device_state_text_view)
        mFunDisconnectLayout = findViewById(R.id.fun_disconnect_layout)
        mFunHrDetectModeLayout = findViewById(R.id.fun_hr_detect_mode_layout)
    }

    private fun initComponent() {
        mFunDisconnectLayout.setOnClickListener {
            mFunDisconnectLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val builder = AlertDialog.Builder(this)
            builder.setTitle("确定断开连接？")
            builder.setCancelable(false)
            builder.setPositiveButton("确定") { _, _ ->
                LsBleManager.getInstance().stopDataReceiveService()
                MemoryVar.device = null
                Toast.makeText(this, "断开连接成功！", Toast.LENGTH_SHORT).show()
                this.finish()
            }
            builder.setNegativeButton("取消") { _, _ ->
                mFunDisconnectLayout.setBackgroundColor(
                    resources.getColor(R.color.white)
                )
            }
            builder.create().show()
        }

        mFunHrDetectModeLayout.setOnClickListener {
            mFunHrDetectModeLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val builder = AlertDialog.Builder(this)
            builder.setTitle("打开/关闭心率监测？")
            builder.setCancelable(false)
            builder.setSingleChoiceItems(
                arrayOf("关闭", "打开"), -1
            ) { p0, p1 ->
                mFunHrDetectModeLayout.setBackgroundColor(resources.getColor(R.color.white))
                Toast.makeText(
                    this@DeviceManageActivity,
                    "设置成功！",
                    Toast.LENGTH_SHORT
                ).show()
                LsBleManager.getInstance()
                    .updatePedometerHeartDetectionMode(
                        MemoryVar.device?.macAddress,
                        HeartRateDetectionMode.values()[p1],
                        object :
                            OnSettingListener() {
                        })
                p0.dismiss()
            }
            builder.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
        resetLayoutColor()
    }

    private fun resetLayoutColor() {
        mFunDisconnectLayout.setBackgroundColor(resources.getColor(R.color.white))
        mFunHrDetectModeLayout.setBackgroundColor(resources.getColor(R.color.white))
    }

    companion object {
        private val TAG = "DeviceManageActivity"
    }

}