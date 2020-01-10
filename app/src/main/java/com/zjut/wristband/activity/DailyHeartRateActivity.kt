package com.zjut.wristband.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.zjut.wristband.R
import com.zjut.wristband.model.DailyHeartInfo
import com.zjut.wristband.util.TimeTransUtil
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll
import java.text.SimpleDateFormat

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

    @SuppressLint("SimpleDateFormat")
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
        mXAxis.setValueFormatter { value, axis ->
            val d = TimeTransUtil.UtcToDate(value.toLong())
            val f = SimpleDateFormat("HH:mm")
            f.format(d)
        }

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
        //mYAxisLeft.setDrawGridLinesBehindData(true)

        setData()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setData() {
        val yVals1 = arrayListOf<Entry>()
        val h = LitePal.where(
            "utc > ? and utc < ?",
            TimeTransUtil.getUtc().toString(),
            TimeTransUtil.getUtc(1).toString()
        ).order("utc asc").find<DailyHeartInfo>()
        if (h.isEmpty()) return
        for (i in h) {
            yVals1.add(Entry((i.utc).toFloat(), i.rate.toFloat()))
        }

        val d = TimeTransUtil.UtcToDate(h[0].utc)
        val f = SimpleDateFormat("M月dd日")
        // create a dataset and give it a type
        val set1 = LineDataSet(yVals1, f.format(d) + " 心率数据")
        //数据对应的是左边还是右边的Y值
        set1.axisDependency = YAxis.AxisDependency.LEFT
        //线条的颜色
        set1.color = ColorTemplate.rgb("00FFFF")
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
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

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val date = TimeTransUtil.UtcToDate(e!!.x.toLong())
            val f = SimpleDateFormat("HH:mm")
            mHeartRateTextView.text = e.y.toString() + "次/分" + "\t" + f.format(date)
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