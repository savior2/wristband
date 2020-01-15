package com.zjut.wristband.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.ReceiveDataCallback
import com.lifesense.ble.SearchCallback
import com.lifesense.ble.bean.*
import com.lifesense.ble.bean.constant.BroadcastType
import com.lifesense.ble.bean.constant.DeviceConnectState
import com.lifesense.ble.bean.constant.DeviceType
import com.lifesense.ble.bean.constant.PacketProfile
import com.zjut.wristband.R
import com.zjut.wristband.model.AerobicsHeartInfo
import com.zjut.wristband.model.DailyHeartInfo
import com.zjut.wristband.service.BleService
import com.zjut.wristband.util.*
import java.util.*

class DeviceConnectFragment : Fragment() {

    private lateinit var mScanDeviceSwitch: Switch
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mDeviceRecyclerView: RecyclerView

    private val mDeviceList = ArrayList<LsDeviceInfo>()
    private val mDeviceAdapter = DeviceAdapter(mDeviceList)


    private val mSearchCallback = MySearchCallback()
    private val mDataCallback = MyDataCallback()
    private var mBleService: BleService? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mBleService = (rawBinder as BleService.MyBinder).getService()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBleService = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_connect, container, false)
        mScanDeviceSwitch = view.findViewById(R.id.scan_device_switch)
        mProgressBar = view.findViewById(R.id.process_bar)
        mDeviceRecyclerView = view.findViewById(R.id.device_recycler_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mScanDeviceSwitch.setOnCheckedChangeListener { p1, isChecked ->
            if (isChecked) {
                openBlueTooth()
            } else {
                stopScan()
            }
        }
        val linearLayoutManager = LinearLayoutManager(this@DeviceConnectFragment.activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mDeviceRecyclerView.layoutManager = linearLayoutManager
        mDeviceRecyclerView.adapter = mDeviceAdapter
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            stopScan()
        } else {
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this@DeviceConnectFragment.activity, BleService::class.java)
        this@DeviceConnectFragment.activity!!.bindService(
            intent,
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        this@DeviceConnectFragment.activity!!.startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        this@DeviceConnectFragment.activity!!.unbindService(mServiceConnection)
    }

    private fun openBlueTooth() {
        if (!LsBleManager.getInstance().isOpenBluetooth) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, ENABLE_BLUETOOTH)
        } else {
            getLocationPermission()
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@DeviceConnectFragment.activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_ACCESS_FINE_LOCATION
            )
        } else {
            beginScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    getLocationPermission()
                } else {
                    mScanDeviceSwitch.isChecked = false
                    Toast.makeText(activity, "扫描失败：应先打开蓝牙", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginScan()
            } else {
                mScanDeviceSwitch.isChecked = false
                Toast.makeText(activity, "扫描失败：应先获取位置权限", Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }
    }


    private fun beginScan() {
        mProgressBar.visibility = View.VISIBLE
        LsBleManager.getInstance()
            .searchLsDevice(mSearchCallback, getDeviceTypes(), BroadcastType.ALL)
    }

    private fun stopScan() {
        mScanDeviceSwitch.isChecked = false
        mProgressBar.visibility = View.GONE
        mDeviceList.clear()
        mDeviceAdapter.notifyDataSetChanged()
        LsBleManager.getInstance().stopSearch()
    }

    private fun getDeviceTypes(): List<DeviceType> {
        return listOf(DeviceType.PEDOMETER)
    }

    private fun sendBindBroadcast(action: String) {
        val intent = Intent(action)
        this@DeviceConnectFragment.activity?.sendBroadcast(intent)
    }


    private inner class MySearchCallback : SearchCallback() {
        override fun onSearchResults(lsDeviceInfo: LsDeviceInfo) {
            var has = false
            if (TextUtils.isEmpty(lsDeviceInfo.macAddress)) {
                return
            }
            for (device in mDeviceList) {
                if (lsDeviceInfo.macAddress == device.macAddress) {
                    has = true
                    break
                }
            }
            if (!has) {
                mDeviceList.add(lsDeviceInfo)
                this@DeviceConnectFragment.activity?.runOnUiThread {
                    mDeviceAdapter.notifyItemChanged(mDeviceList.size)
                }
            }
        }
    }

    private inner class MyDataCallback : ReceiveDataCallback() {
        override fun onDeviceConnectStateChange(p0: DeviceConnectState?, p1: String?) {
            super.onDeviceConnectStateChange(p0, p1)
            when (p0) {
                DeviceConnectState.CONNECTED_SUCCESS -> {
                    Log.e(TAG, "connect success")
                }
                DeviceConnectState.DISCONNECTED -> {
                    Log.e(TAG, "disconnect")
                }
                else -> {

                }
            }
            Log.e(TAG, "$p0")
        }

        override fun onReceivePedometerMeasureData(p0: Any?, p1: PacketProfile?, p2: String?) {
            super.onReceivePedometerMeasureData(p0, p1, p2)
            Log.e(TAG, "onReceivePedometerMeasureData: $p0")
            when (p0) {
                is List<*> -> {
                    val stat = p0[p0.size - 1] as PedometerData
                    Log.e(
                        TAG,
                        "onReceivePedometerMeasureData: time=${stat.measureTime} steps=${stat.walkSteps}"
                    )
                    val sp =
                        SharedPreUtil(this@DeviceConnectFragment.activity!!, SharedPreFile.STATUS)
                    sp.editor.putString(SharedPreKey.TIME, stat.measureTime.toString())
                    sp.editor.putInt(SharedPreKey.STEP, stat.walkSteps)
                    sp.editor.apply()
                }
                is PedometerHeartRateData -> {
                    for (i in 0 until p0.heartRates.size) {
                        val data = DailyHeartInfo(p0.heartRates[i] as Int, p0.utc + i * 300, 0)
                        data.save()
                    }
                }
                is PedometerSleepData -> {

                }
            }
        }

        override fun onReceiveRealtimeMeasureData(p0: String?, p1: Any?) {
            if (p1 is PedometerHeartRateData) {
                Log.e(TAG, "real data: $p0, ${p1.heartRates}")
                if (MemoryVar.aerobicsNum != null) {
                    val info = AerobicsHeartInfo(
                        p1.heartRates[0] as Int, p1.utc,
                        MemoryVar.aerobicsNum!!, 0
                    )
                    info.save()
                }
                Log.e(TAG, "real data: $p0, ${p1.heartRates}")
            }
            super.onReceiveRealtimeMeasureData(p0, p1)
        }


        override fun onReceivePedometerData(p0: PedometerData?) {
            super.onReceivePedometerData(p0)
            Log.e(TAG, "onReceivePedometerData: $p0")
        }

        override fun onPedometerSportsModeNotify(p0: String?, p1: SportNotify?) {
            Log.e(TAG, "sports mode: $p0, ${p1?.toString()}")
            super.onPedometerSportsModeNotify(p0, p1)
        }
    }

    //internal很重要
    private inner class DeviceHolder internal constructor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_device, parent, false)),
        View.OnClickListener {
        private lateinit var mDevice: LsDeviceInfo
        private val mDeviceNameTextView: TextView =
            itemView.findViewById(R.id.device_name_text_view)
        private val mDeviceAddressTextView: TextView =
            itemView.findViewById(R.id.device_address_text_view)

        init {
            itemView.setOnClickListener(this)
        }

        internal fun bind(lsDeviceInfo: LsDeviceInfo) {
            mDevice = lsDeviceInfo
            mDeviceNameTextView.text = lsDeviceInfo.deviceName
            mDeviceAddressTextView.text = lsDeviceInfo.macAddress
        }

        override fun onClick(view: View) {
            itemView.setOnClickListener {
                itemView.setBackgroundColor(resources.getColor(R.color.grey))
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("确定连接此手环？")
                builder.setCancelable(false)
                builder.setPositiveButton("确定") { _, _ ->
                    LsBleManager.getInstance().stopDataReceiveService()
                    LsBleManager.getInstance().setMeasureDevice(null)
                    LsBleManager.getInstance().addMeasureDevice(mDevice)
                    LsBleManager.getInstance().startDataReceiveService(mDataCallback)
                    MemoryVar.device = mDevice
                    //mBleService?.connect(BluetoothAdapter.getDefaultAdapter(), mDevice.macAddress)
                    Toast.makeText(
                        this@DeviceConnectFragment.activity,
                        "连接成功！",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    itemView.setBackgroundColor(
                        resources.getColor(
                            R.color.white
                        )
                    )
                    sendBindBroadcast(BroadType.ACTION_DEVICE_BIND)
                }
                builder.setNegativeButton("取消") { _, _ ->
                    itemView.setBackgroundColor(
                        resources.getColor(
                            R.color.white
                        )
                    )
                }
                builder.create().show()
            }
        }
    }

    private inner class DeviceAdapter constructor(private val mDevices: List<LsDeviceInfo>) :
        RecyclerView.Adapter<DeviceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            val layoutInflater = LayoutInflater.from(this@DeviceConnectFragment.activity!!)
            return DeviceHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
            val lsDeviceInfo = mDevices[position]
            holder.bind(lsDeviceInfo)
        }

        override fun getItemCount(): Int {
            return mDevices.size
        }
    }


    companion object {
        private val TAG = "DeviceConnectFragment"
        private val ENABLE_BLUETOOTH = 1
        private val REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }
}