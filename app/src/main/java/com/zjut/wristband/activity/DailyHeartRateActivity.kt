package com.zjut.wristband.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.zjut.wristband.R
import java.text.SimpleDateFormat
import java.util.*

class DailyHeartRateActivity : AppCompatActivity() {
    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    private lateinit var mLineChart: LineChart
    private lateinit var mMarkerView: MarkerView
    private lateinit var mXAxis: XAxis
    private lateinit var mYAxisLeft: YAxis
    private lateinit var mYAxisRight: YAxis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_heart_rate)
        mTitleTextView = findViewById(R.id.title_text_view)
        mBackButton = findViewById(R.id.back_button)
        mLineChart = findViewById(R.id.heart_rate_line_chart)
        mMarkerView = MyMarkerView(this, R.layout.module_heart_rate_chat)
        mXAxis = mLineChart.xAxis
        mYAxisLeft = mLineChart.axisLeft
        mYAxisRight = mLineChart.axisRight
        initView()
    }

    private fun initView() {
        mTitleTextView.text = "日常心率"
        mBackButton.visibility = View.VISIBLE
        mBackButton.setOnClickListener { finish() }

        mLineChart.setBackgroundColor(ColorTemplate.rgb("4682B4"))
        mLineChart.description.isEnabled = false
        mLineChart.setNoDataText("今天未作记录")
        mLineChart.animateX(2500)

        mMarkerView.chartView = mLineChart
        mLineChart.marker = mMarkerView

        mXAxis.setDrawGridLines(false)
        mXAxis.position = XAxis.XAxisPosition.BOTTOM
        mXAxis.setAvoidFirstLastClipping(true)
        mXAxis.setLabelCount(3, false)
        val mFormat = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)
       /* mXAxis.setValueFormatter { value, axis ->
            mFormat.format(Date(value.toLong()))
        }*/

        mYAxisLeft.setDrawGridLines(false)
        mYAxisRight.setDrawGridLines(false)
        mYAxisLeft.setLabelCount(5, false)
        mYAxisRight.setLabelCount(5, false)
        val normal = LimitLine(72f, "")
        normal.lineColor = Color.GREEN
        val quick = LimitLine(100f, "")
        quick.lineColor = Color.RED
        val slow = LimitLine(60f, "")
        slow.lineColor = Color.YELLOW
        mYAxisLeft.addLimitLine(normal)
        mYAxisLeft.addLimitLine(quick)
        mYAxisLeft.addLimitLine(slow)
        mYAxisLeft.setDrawGridLinesBehindData(true)

        setData(200, 150)
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
        set1.color = ColorTemplate.rgb("00FFFF")
        //表中数据圆点的颜色
        set1.setDrawCircles(false)
        //表中数据线条的宽度
        set1.lineWidth = 1f
        //点中的十字的颜色
        set1.highLightColor = Color.rgb(255, 117, 50)
        val data = LineData(set1)
        mLineChart.data = data
    }

    private class MyMarkerView(context: Context, layoutResource: Int) :
        MarkerView(context, layoutResource) {
        private val mHeartRateTextView: TextView = findViewById(R.id.heart_rate_text_view)

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            mHeartRateTextView.text = e?.y.toString()
            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
            return MPPointF(-15f, -height.toFloat())
        }

    }

    companion object {
        private val TAG = "DailyHeartRateActivity"
    }
}