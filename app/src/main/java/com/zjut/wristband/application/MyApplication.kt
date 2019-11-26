package com.zjut.wristband.application

import android.app.Application
import com.baidu.mapapi.SDKInitializer
import com.lifesense.ble.LsBleManager
import org.litepal.LitePal


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //initialize baidu SDK
        SDKInitializer.initialize(this)

        //initialize litepal
        LitePal.initialize(this)

        //init LSBluetoothManager
        LsBleManager.getInstance().initialize(applicationContext)

        //register bluetooth broadcast receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(applicationContext)

        //register message service
        LsBleManager.getInstance().registerMessageService()


    }
}