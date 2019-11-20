package com.zjut.wristband.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.SearchCallback
import com.lifesense.ble.bean.LsDeviceInfo
import com.lifesense.ble.bean.constant.BroadcastType
import com.lifesense.ble.bean.constant.DeviceType
import com.zjut.wristband.R
import java.util.*

class DeviceConnectFragment : Fragment() {

    private lateinit var mScanDeviceSwitch: Switch
    private lateinit var mDeviceRecyclerView: RecyclerView

    private val mDeviceList = ArrayList<LsDeviceInfo>()
    private val mDeviceAdapter = DeviceAdapter(mDeviceList)


    private val mSearchCallback = MySearchCallback()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_connect, container, false)
        mScanDeviceSwitch = view.findViewById(R.id.scan_device_switch)
        mDeviceRecyclerView = view.findViewById(R.id.device_recycler_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mScanDeviceSwitch.setOnCheckedChangeListener { p1, isChecked ->
            if (isChecked) {
                openBlueTooth()
            } else {
                mDeviceList.clear()
                mDeviceAdapter.notifyDataSetChanged()
                LsBleManager.getInstance().stopSearch()
            }
        }
        val linearLayoutManager = LinearLayoutManager(this@DeviceConnectFragment.activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mDeviceRecyclerView.layoutManager = linearLayoutManager
        mDeviceRecyclerView.adapter = mDeviceAdapter
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
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE_ACCESS_COARSE_LOCATION
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
            REQUEST_CODE_ACCESS_COARSE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        LsBleManager.getInstance()
            .searchLsDevice(mSearchCallback, getDeviceTypes(), BroadcastType.ALL)
    }

    private fun getDeviceTypes(): List<DeviceType> {
        return listOf(DeviceType.PEDOMETER)
    }


    private inner class MySearchCallback : SearchCallback() {
        override fun onSearchResults(lsDeviceInfo: LsDeviceInfo) {
            var has = false
            if (TextUtils.isEmpty(lsDeviceInfo.macAddress)) {
                return
            }
            for (device in mDeviceList) {
                if (lsDeviceInfo.macAddress == device.getMacAddress()) {
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

    //internal很重要
    private inner class DeviceHolder internal constructor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_device, parent, false)),
        View.OnClickListener {
        private val mDeviceNameTextView: TextView =
            itemView.findViewById(R.id.device_name_text_view)
        private val mDeviceAddressTextView: TextView =
            itemView.findViewById(R.id.device_address_text_view)

        init {
            itemView.setOnClickListener(this)
        }

        internal fun bind(lsDeviceInfo: LsDeviceInfo) {
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
                    Toast.makeText(this@DeviceConnectFragment.activity, "连接成功！", Toast.LENGTH_SHORT)
                        .show()
                    itemView.setBackgroundColor(
                        resources.getColor(
                            R.color.white
                        )
                    )
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
        private val REQUEST_CODE_ACCESS_COARSE_LOCATION = 1
    }
}