package com.zjut.wristband.service

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import java.util.*

class BleService : Service() {

    private var mBluetoothGatt: BluetoothGatt? = null
    private val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt?.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            // 发现GATT服务
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setBleNotification()
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val format = BluetoothGattCharacteristic.FORMAT_UINT8
            Log.e(TAG, "心率：" + characteristic.getIntValue(format, 1))
        }
    }


    private fun setBleNotification() {
        if (mBluetoothGatt == null) {
            return
        }

        // 获取蓝牙设备的服务
        val gattService = mBluetoothGatt?.getService(convertFromInteger(GATT_SERVICE)) ?: return

        // 获取蓝牙设备的特征
        val gattCharacteristic = gattService.getCharacteristic(
            convertFromInteger(
                GATT_CHARACTERISTIC
            )
        ) ?: return

        // 获取蓝牙设备特征的描述符
        val descriptor = gattCharacteristic.getDescriptor(convertFromInteger(GATT_DESCRIPTOR))
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        if (mBluetoothGatt?.writeDescriptor(descriptor)!!) {
            // 蓝牙设备在数据改变时，通知App，App在收到数据后回调onCharacteristicChanged方法
            mBluetoothGatt?.setCharacteristicNotification(gattCharacteristic, true)
        }
    }

    fun connect(bluetoothAdapter: BluetoothAdapter?, address: String): Boolean {
        if (bluetoothAdapter == null || TextUtils.isEmpty(address)) {
            return false
        }

        val device = bluetoothAdapter.getRemoteDevice(address) ?: return false
        mBluetoothGatt = device.connectGatt(this@BleService, false, mGattCallback)
        return true
    }

    fun disconnect() = mBluetoothGatt?.disconnect()
    fun release() = mBluetoothGatt?.close()

    private fun convertFromInteger(i: Long): UUID {
        val MSB: Long = 0x1000
        val LSB = -0x7fffff7fa064cb05L
        return UUID(MSB or (i shl 32), LSB)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        release()
        return super.onUnbind(intent)
    }

    inner class MyBinder : Binder() {
        fun getService() = this@BleService
    }

    companion object {
        private val TAG = "BleService"
        private val GATT_SERVICE: Long = 0x180D
        private val GATT_CHARACTERISTIC: Long = 0x2A37
        private val GATT_DESCRIPTOR: Long = 0x2902
    }
}