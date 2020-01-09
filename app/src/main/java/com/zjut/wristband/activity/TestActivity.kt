package com.zjut.wristband.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.OnSettingListener
import com.lifesense.ble.bean.SportRequestInfo
import com.lifesense.ble.bean.constant.PedometerSportsType
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar
import com.zjut.wristband.util.SharedPreFile
import com.zjut.wristband.util.SharedPreKey
import com.zjut.wristband.util.SharedPreUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


class TestActivity : AppCompatActivity(), OnChartValueSelectedListener {
    override fun onNothingSelected() {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }


    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button


    private lateinit var mStartButton: Button
    private lateinit var mStopButton: Button
    private lateinit var mUploadButton: Button

    private lateinit var mLineChart: LineChart

    private lateinit var mToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mTitleTextView = findViewById(R.id.title_text_view)
        mBackButton = findViewById(R.id.back_button)
        mTitleTextView.text = "系统测试"
        mBackButton.visibility = View.VISIBLE
        mBackButton.setOnClickListener { finish() }
        mStartButton = findViewById(R.id.start)
        mStopButton = findViewById(R.id.stop)
        mUploadButton = findViewById(R.id.sports)
        mLineChart = findViewById(R.id.line_chart)
        mToken = SharedPreUtil(this, SharedPreFile.ACCOUNT).getString(SharedPreKey.TOKEN) ?: ""
        run()
        upload()
        initLineChart()

    }


    private fun initLineChart() {
        mLineChart.setOnChartValueSelectedListener(this)
        mLineChart.setNoDataText("没有数据")
        mLineChart.setBackgroundColor(resources.getColor(R.color.blue_sky))
        setData(10, 100)
    }

    private fun setData(count: Int, range: Int) {
        val yVals1 = arrayListOf<Entry>()
        val yVals2 = arrayListOf<Entry>()
        val yVals3 = arrayListOf<Entry>()

        for (i in 1..count) {
            val mult = range / 2f
            val value = (Math.random() * mult) + 50
            yVals1.add(Entry(i.toFloat(), value.toFloat()))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(yVals1, "数据1")
        //数据对应的是左边还是右边的Y值
        set1.axisDependency = YAxis.AxisDependency.LEFT
        //线条的颜色
        set1.color = Color.BLUE
        //表中数据圆点的颜色
        set1.setCircleColor(Color.RED)
        //表中数据线条的宽度
        set1.lineWidth = 2f
        //表中数据圆点的半径
        set1.circleRadius = 3f
        //设置线面部分是否填充
        set1.setDrawFilled(true)
        //填充的颜色透明度
        set1.fillAlpha = 100
        //填充颜色
        set1.fillColor = ColorTemplate.rgb("000000")
        //点中的十字的颜色
        set1.highLightColor = Color.rgb(255, 117, 50)
        //绘制的数据的圆点是否是实心还是空心
        set1.setDrawCircleHole(true)
        val data = LineData(set1)
        data.setValueTextColor(Color.YELLOW)
        mLineChart.data = data
    }

    private fun run() {
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

    private fun upload() {
        mUploadButton.setOnClickListener {
            Thread {
                val body = RequestBody.create(
                    JSON, "{\n" +
                            "\t\"token\": \"$mToken\",\n" +
                            "\t\"deviceId\": \"EE:C5:2E:A6:D1:39\",\n" +
                            "\t\"mode\": \"running\",\n" +
                            "\t\"detail\": [{\n" +
                            "\t\t\"time\": \"1573456498786\",\n" +
                            "\t\t\"heartRate\": \"100\",\n" +
                            "\t\t\"position\": \"null\"\n" +
                            "\t}, {\n" +
                            "\t\t\"time\": \"1573456498786\",\n" +
                            "\t\t\"heartRate\": \"100\",\n" +
                            "\t\t\"position\": \"null\"\n" +
                            "\t}]\n" +
                            "}"
                )
                val request =
                    Request.Builder().url("http://47.99.157.159:9898/uploadSportData").post(body)
                        .build()
                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""
                Log.e(TAG, responseString)
            }.start()
        }
    }

    companion object {
        private val TAG = "TestActivity"
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }
}