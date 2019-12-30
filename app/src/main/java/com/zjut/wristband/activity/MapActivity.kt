package com.zjut.wristband.activity

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.zjut.wristband.R
import com.zjut.wristband.util.DistanceUtil
import com.zjut.wristband.util.LogUtil

class MapActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    private lateinit var mDistanceTextView: TextView
    private lateinit var mTimeSpanTextView: TextView
    private lateinit var mSpeedTextView: TextView

    private lateinit var mStartRunButton: Button
    private lateinit var mFinishRunButton: Button
    private lateinit var mSearchBarRelativeLayout: RelativeLayout
    private lateinit var mSearchTextView: TextView

    private lateinit var mMapView: MapView
    private lateinit var mBaiduMap: BaiduMap
    private lateinit var mSensorManager: SensorManager


    private val mLocClient = LocationClient(this)
    private val mLocListener = MyListener()

    private var mCurrentDirection = 0
    private var mCurrentLat = 0.0
    private var mCurrentLon = 0.0
    private var mCurrentZoom = 20.0f
    private var mIsFirstLoc = true
    private var mLastX = 0.0
    private var mLastPoint = LatLng(0.0, 0.0)

    private val mPoints = arrayListOf<LatLng>()
    private var mRunTime = 0L
    private var mDistance = 0.0

    private val startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    private val finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initView()
        initMap()
    }

    private fun initView() {
        mTitleTextView = findViewById(R.id.title_text_view)
        mTitleTextView.text = "跑步"
        mBackButton = findViewById(R.id.back_button)
        mBackButton.setOnClickListener { this.finish() }
        mBackButton.visibility = View.VISIBLE
        mDistanceTextView = findViewById(R.id.distance_text_view)
        mTimeSpanTextView = findViewById(R.id.time_span_text_view)
        mSpeedTextView = findViewById(R.id.speed_text_view)
        mStartRunButton = findViewById(R.id.start_run_button)
        mStartRunButton.setOnClickListener {
            if (!mLocClient.isStarted) {
                mTimeSpanTextView.text = "00:00:00"
                mDistanceTextView.text = "0.00"
                mSearchBarRelativeLayout.visibility = View.VISIBLE
                mBaiduMap.clear()
                mLocClient.start()
            }
        }
        mFinishRunButton = findViewById(R.id.finish_run_button)
        mFinishRunButton.setOnClickListener {
            mSearchBarRelativeLayout.visibility = View.GONE
            if (mLocClient.isStarted) {
                mLocClient.stop()
                mLastPoint = LatLng(0.0, 0.0)
                if (mIsFirstLoc) {
                    mPoints.clear()
                    return@setOnClickListener
                }

                val oFinish = MarkerOptions()// 地图标记覆盖物参数配置类
                oFinish.position(mPoints[mPoints.size - 1])
                oFinish.icon(finishBD)
                mBaiduMap.addOverlay(oFinish) // 在地图上添加此图层
                mPoints.clear()
                mIsFirstLoc = true
                mSpeedTextView.text = "0"
                mDistance = 0.0
                mRunTime = 0

            }
        }
        mSearchBarRelativeLayout = findViewById(R.id.search_progress_bar)
        mSearchTextView = findViewById(R.id.search_text_view)
    }

    private fun initMap() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mMapView = findViewById(R.id.map)
        mBaiduMap = mMapView.map
        mBaiduMap.isMyLocationEnabled = true
        mBaiduMap.setMyLocationConfiguration(
            MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null
            )
        )
        mBaiduMap.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(p0: MapStatus?) {
            }

            override fun onMapStatusChangeStart(p0: MapStatus?, p1: Int) {
            }

            override fun onMapStatusChange(p0: MapStatus?) {
            }

            override fun onMapStatusChangeFinish(p0: MapStatus?) {
                if (!mIsFirstLoc) {
                    mCurrentZoom = p0!!.zoom
                }
            }
        })

        mLocClient.registerLocationListener(mLocListener)
        val option = LocationClientOption()
        //option.locationMode = LocationClientOption.LocationMode.Device_Sensors//只用gps定位，需要在室外定位。
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(1000)
        mLocClient.locOption = option
    }

    private fun getMostAccuracyLocation(location: BDLocation): LatLng? {
        if (location.radius > 40) { //gps位置精度大于40米的点直接弃用
            return null
        }

        val ll = LatLng(location.latitude, location.longitude)

        if (DistanceUtil.getDistance(mLastPoint, ll) > 10) {
            mLastPoint = ll
            mPoints.clear()  //有任意连续两点位置大于10，重新取点
            return null
        }
        mPoints.add(ll)
        mLastPoint = ll
        LogUtil.e(TAG, "size: ${mPoints.size}")
        //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
        if (mPoints.size >= 5) {
            LogUtil.e(TAG, "size: ${mPoints.size}")
            mPoints.clear()
            return ll
        }
        return null
    }

    private fun locate(ll: LatLng) {
        mCurrentLat = ll.latitude
        mCurrentLon = ll.longitude
        val mLocData = MyLocationData.Builder().accuracy(0f)
            .direction(mCurrentDirection.toFloat()).latitude(ll.latitude)
            .longitude(ll.longitude).build()
        LogUtil.e(TAG, "map: ${mBaiduMap}")
        LogUtil.e(TAG, "locData: ${mLocData}")
        mBaiduMap.setMyLocationData(mLocData)

        val builder = MapStatus.Builder()
        builder.target(ll).zoom(mCurrentZoom)
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    private fun setRunTime(seconds: Long) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        val time = String.format("%02d:%02d:%02d", hours, minutes, secs)
        mTimeSpanTextView.text = time
    }

    private inner class MyListener : BDLocationListener {
        @SuppressLint("SetTextI18n")
        override fun onReceiveLocation(p0: BDLocation?) {
            if (p0 == null) {
                return
            }


            //注意这里只接受gps点，需要在室外定位
            if (p0.locType == BDLocation.TypeGpsLocation) {
                if (mIsFirstLoc) {
                    val ll = getMostAccuracyLocation(p0) ?: return
                    LogUtil.e(TAG, "zoom：$mCurrentZoom")
                    mIsFirstLoc = false
                    mPoints.add(ll)
                    locate(ll)
                    val oStart = MarkerOptions()
                    oStart.position(ll)
                    oStart.icon(startBD)
                    mBaiduMap.addOverlay(oStart)
                    return
                }

                //从第二个点开始
                mSearchBarRelativeLayout.visibility = View.GONE
                setRunTime(++mRunTime)
                val ll = LatLng(p0.latitude, p0.longitude)
                mSpeedTextView.text = "${p0.speed} km/h"
                if (DistanceUtil.getDistance(mLastPoint, ll) < 5) {
                    return
                }

                mDistance += DistanceUtil.getDistance(mLastPoint, ll)
                mDistanceTextView.text = String.format("%.2f", mDistance)
                locate(ll)
                mPoints.add(ll)
                mLastPoint = ll
                mBaiduMap.clear()

                //起始点图层也会被清除，重新绘画
                val oStart = MarkerOptions()
                oStart.position(mPoints[0])
                oStart.icon(startBD)
                mBaiduMap.addOverlay(oStart)

                //将points集合中的点绘制轨迹线条图层，显示在地图上
                val ooPolyline = PolylineOptions().width(10).color(-0x55010000).points(mPoints)
                mBaiduMap.addOverlay(ooPolyline)
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val x = p0!!.values[SensorManager.DATA_X].toDouble()
        if (mIsFirstLoc) {
            mLastX = x
            return
        }
        if (Math.abs(x - mLastX) > 1.0) {
            mCurrentDirection = x.toInt()
            val mLocData = MyLocationData.Builder().accuracy(0f)
                .direction(mCurrentDirection.toFloat()).latitude(mCurrentLat)
                .longitude(mCurrentLon)
                .build()
            mBaiduMap.setMyLocationData(mLocData)
        }
        mLastX = x
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
        mSensorManager.unregisterListener(this)

    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
        mSensorManager.registerListener(
            this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onDestroy() {
        // 退出时销毁定位
        mLocClient.unRegisterLocationListener(mLocListener)
        if (mLocClient.isStarted) {
            mLocClient.stop()
        }
        // 关闭定位图层
        startBD?.recycle()
        finishBD?.recycle()
        mBaiduMap.isMyLocationEnabled = false
        mMapView.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MapActivity"
    }
}