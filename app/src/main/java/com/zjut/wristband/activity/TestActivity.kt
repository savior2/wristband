package com.zjut.wristband.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.baidu.lbsapi.auth.LBSAuthManager
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.OnSettingListener
import com.lifesense.ble.bean.SportRequestInfo
import com.lifesense.ble.bean.constant.PedometerSportsType
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar

class TestActivity : AppCompatActivity() {

    private lateinit var mStartButton: Button
    private lateinit var mStopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mStartButton = findViewById(R.id.start)
        mStopButton = findViewById(R.id.stop)

        mStartButton.setOnClickListener {
            LsBleManager.getInstance()
                .setRealtimeHeartRateSyncState(MemoryVar.device?.macAddress, true, object :
                    OnSettingListener() {
                    override fun onSuccess(p0: String?) {
                        Log.e(TAG, "set real start success: $p0")
                        super.onSuccess(p0)
                    }

                    override fun onFailure(p0: Int) {
                        Log.e(TAG, "set real start fail: $p0")
                        super.onFailure(p0)
                    }
                })
            val info = SportRequestInfo(PedometerSportsType.RUNNING)
            info.state = SportRequestInfo.START
            LsBleManager.getInstance()
                .pushDeviceMessage(MemoryVar.device?.macAddress, info, object :
                    OnSettingListener() {
                    override fun onSuccess(p0: String?) {
                        Log.e(TAG, "start success: $p0")
                        super.onSuccess(p0)
                    }

                    override fun onFailure(p0: Int) {
                        Log.e(TAG, "start fail: $p0")
                        super.onFailure(p0)
                    }
                })
        }
        mStopButton.setOnClickListener {
            LsBleManager.getInstance()
                .setRealtimeHeartRateSyncState(MemoryVar.device?.macAddress, false, object :
                    OnSettingListener() {
                    override fun onSuccess(p0: String?) {
                        Log.e(TAG, "set real stop success: $p0")
                        super.onSuccess(p0)
                    }

                    override fun onFailure(p0: Int) {
                        Log.e(TAG, "set real stop fail: $p0")
                        super.onFailure(p0)
                    }
                })
            val info = SportRequestInfo(PedometerSportsType.RUNNING)
            info.state = SportRequestInfo.STOP
            LsBleManager.getInstance()
                .pushDeviceMessage(MemoryVar.device?.macAddress, info, object :
                    OnSettingListener() {
                    override fun onSuccess(p0: String?) {
                        super.onSuccess(p0)
                        Log.e(TAG, "stop success: $p0")
                    }

                    override fun onFailure(p0: Int) {
                        super.onFailure(p0)
                        Log.e(TAG, "stop fail: $p0")
                    }
                })
        }
    }

    companion object {
        private val TAG = "TestActivity"
    }
}