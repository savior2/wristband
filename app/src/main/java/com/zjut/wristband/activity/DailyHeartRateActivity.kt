package com.zjut.wristband.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.zjut.wristband.R

class DailyHeartRateActivity : AppCompatActivity() {
    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    private lateinit var mLineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_heart_rate)
        mTitleTextView = findViewById(R.id.title_text_view)
        mBackButton = findViewById(R.id.back_button)
        mLineChart = findViewById(R.id.heart_rate_line_chart)
        initView()
    }

    private fun initView() {
        mTitleTextView.text = "日常心率"
        mBackButton.visibility = View.VISIBLE
        mBackButton.setOnClickListener { finish() }
        mLineChart.setBackgroundColor(resources.getColor(R.color.blue_sky))
        setData(20,100)
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

    companion object {
        private val TAG = "DailyHeartRateActivity"
    }
}