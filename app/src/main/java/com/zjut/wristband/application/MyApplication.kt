package com.zjut.wristband.application

import android.app.Application
import com.lifesense.ble.LsBleManager
import org.litepal.LitePal


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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