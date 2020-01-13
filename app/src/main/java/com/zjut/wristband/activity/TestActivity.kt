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
import com.zjut.wristband.model.AerobicsHeartInfo
import com.zjut.wristband.model.AerobicsPositionInfo
import com.zjut.wristband.model.DailyHeartInfo
import com.zjut.wristband.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.litepal.LitePal


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
    private lateinit var mTimeTransferButton: Button

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
        mTimeTransferButton = findViewById(R.id.time_transfer)
        mToken = SharedPreUtil(this, SharedPreFile.ACCOUNT).getString(SharedPreKey.TOKEN) ?: ""
        run()
        upload()
        initLineChart()
        mTimeTransferButton.setOnClickListener {
            Log.e(TAG, TimeTransUtil.UtcToDate(1578579281L).toString())
            LitePal.getDatabase()
            val aa = DailyHeartInfo(11, 11, 0)
            aa.save()
        }

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
            /*Thread {
                val body = RequestBody.create(
                    JSON,
                    "{\"deviceId\":\"EE:C5:2E:A6:D1:39\",\"heartRate\":\"1578824080661,82,1578824082055,82,1578824082648,82,1578824083642,82,1578824084639,82,1578824085631,82,1578824086624,82,1578824087619,82,1578824088611,82,1578824089605,82,1578824090598,82,1578824091592,81,1578824278617,87,1578824279609,87,1578824280603,87,1578824281597,87,1578824282789,87,1578824283584,87,1578824284579,87,1578824285572,87,1578824286565,87,1578824287759,88,1578824288553,89,1578824289547,90,1578824290541,90,1578824291534,90,1578824292726,90,1578824293721,90,1578824294714,91,1578824295709,92,\",\"position\":\"1578758400236,lng120.050536,lat30.236678,1578758400260,lng120.050539,lat30.236683,1578758400252,lng120.05054,lat30.236691,1578758400258,lng120.05054,lat30.236691,1578758400267,lng120.050529,lat30.236705,1578758400264,lng120.050526,lat30.236737,1578758400269,lng120.050533,lat30.236768,1578758400264,lng120.05053,lat30.236764,1578758400289,lng120.050527,lat30.236761,1578758400287,lng120.050516,lat30.236758,1578758400316,lng120.050513,lat30.236752,1578758400296,lng120.050526,lat30.236739,1578758400298,lng120.050536,lat30.23674,1578758400478,lng120.050505,lat30.236706,1578758400466,lng120.0505,lat30.236712,1578758400467,lng120.050496,lat30.236721,1578758400478,lng120.0505,lat30.236706,1578758400513,lng120.050506,lat30.236699,1578758400503,lng120.050512,lat30.236703,1578758400504,lng120.050518,lat30.236689,1578758400534,lng120.050525,lat30.236694,1578758400560,lng120.050513,lat30.236689,1578758400601,lng120.050511,lat30.236693,1578758400559,lng120.050513,lat30.236698,1578758400564,lng120.050513,lat30.236698,1578758400561,lng120.050513,lat30.236698,1578758400561,lng120.050491,lat30.236719,1578758400562,lng120.05044,lat30.236763,1578758400563,lng120.050403,lat30.236777,1578758400567,lng120.050354,lat30.236734,1578758400563,lng120.050322,lat30.236713,1578758400562,lng120.050299,lat30.236704,\",\"speed\":\"1578758400236,0.0,1578758400260,0.0,1578758400252,0.0,1578758400258,0.0,1578758400267,0.0,1578758400264,0.0,1578758400269,0.0,1578758400264,0.0,1578758400289,0.0,1578758400287,1.9800000190734863,1578758400316,1.5119999647140503,1578758400296,3.384000062942505,1578758400298,2.124000072479248,1578758400478,0.0,1578758400466,0.0,1578758400467,0.7200000286102295,1578758400478,0.0,1578758400513,2.2320001125335693,1578758400503,2.124000072479248,1578758400504,3.312000036239624,1578758400534,2.2320001125335693,1578758400560,0.0,1578758400601,0.0,1578758400559,1.0800000429153442,1578758400564,1.0800000429153442,1578758400561,1.0800000429153442,1578758400561,4.535999774932861,1578758400562,10.043999671936035,1578758400563,10.727999687194824,1578758400567,10.368000030517578,1578758400563,8.640000343322754,1578758400562,8.315999984741211,\",\"startTime\":\"1578758400600\",\"token\":\"7e0188af56bf8e9ec7419b3946ba882e\"}"
                )
                val request =
                    Request.Builder().url("http://47.99.157.159:9898/uploadSportData").post(body)
                        .build()
                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""
                Log.e(TAG, responseString)
            }.start()*/
            val positionInfo = AerobicsPositionInfo()
            positionInfo.status = 1
            positionInfo.updateAll("status = 0")
        }
    }

    companion object {
        private val TAG = "TestActivity"
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }
}