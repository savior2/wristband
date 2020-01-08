package com.zjut.wristband.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import com.shinelw.library.ColorArcProgressBar
import com.zjut.wristband.R
import com.zjut.wristband.activity.DailyHeartRateActivity
import com.zjut.wristband.util.MemoryVar
import com.zjut.wristband.util.SharedPreFile
import com.zjut.wristband.util.SharedPreKey
import com.zjut.wristband.util.SharedPreUtil
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var mProcessBar: ColorArcProgressBar

    private lateinit var mSwipeRefreshLayout: SuperSwipeRefreshLayout
    private lateinit var mRefreshTextView: TextView
    private lateinit var mRefreshProgressBar: ProgressBar
    private lateinit var mRefreshImageView: ImageView

    private lateinit var mScrollView: ScrollView
    private lateinit var mDailyHeartRateLayout: RelativeLayout

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mProcessBar = view.findViewById(R.id.process_bar)
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        val swipeHeader = LayoutInflater.from(mSwipeRefreshLayout.context)
            .inflate(R.layout.module_refresh_header, null)
        mRefreshTextView = swipeHeader.findViewById(R.id.refresh_text_view)
        mRefreshProgressBar = swipeHeader.findViewById(R.id.refresh_progress_bar)
        mRefreshImageView = swipeHeader.findViewById(R.id.refresh_image_view)
        mSwipeRefreshLayout.setHeaderView(swipeHeader)
        mScrollView = view.findViewById(R.id.scroll_view)
        mDailyHeartRateLayout = view.findViewById(R.id.home_daily_heart_rate__layout)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSteps()
        mRefreshTextView.text = "下拉刷新"
        mScrollView.viewTreeObserver.addOnScrollChangedListener {
            //mSwipeRefreshLayout.isRefreshing = (mScrollView.scaleY == 0f)
        }
        mSwipeRefreshLayout.setOnPullRefreshListener(object :
            SuperSwipeRefreshLayout.OnPullRefreshListener {
            override fun onPullEnable(p0: Boolean) {
                mRefreshTextView.text = if (p0) "松开刷新" else "下拉刷新"
                mRefreshImageView.visibility = View.VISIBLE
                mRefreshImageView.rotation = if (p0) 180f else 0f
            }

            override fun onPullDistance(p0: Int) {
            }

            override fun onRefresh() {
                mRefreshTextView.text = "正在刷新"
                mRefreshImageView.visibility = View.GONE
                mRefreshProgressBar.visibility = View.VISIBLE
                setSteps()
                mHandler.postDelayed({
                    mSwipeRefreshLayout.isRefreshing = false
                    mRefreshProgressBar.visibility = View.GONE
                }, 2000)
            }
        })
        initLayoutClick()
    }

    private fun initLayoutClick() {
        mDailyHeartRateLayout.setOnClickListener {
            mDailyHeartRateLayout.setBackgroundColor(resources.getColor(R.color.grey))
            val intent = Intent(activity, DailyHeartRateActivity::class.java)
            startActivity(intent)
        }
        mDailyHeartRateLayout.setOnHoverListener { p0, p1 ->
            when (p1.action) {
                MotionEvent.ACTION_HOVER_MOVE -> {
                    mDailyHeartRateLayout.setBackgroundColor(resources.getColor(R.color.grey))
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    mDailyHeartRateLayout.setBackgroundColor(resources.getColor(R.color.white))
                }
            }
            true
        }
    }

    private fun setSteps() {
        val sp = SharedPreUtil(activity!!, SharedPreFile.STATUS)
        val time = sp.sp.getString(SharedPreKey.TIME, "")
        val steps = sp.sp.getInt(SharedPreKey.STEP, 0)
        if (TextUtils.isEmpty(time)) {
            mProcessBar.setCurrentValues(0f)
            return
        }
        val d0 = Date().date.toString()
        val d1 = time!!.split(" ")[2]
        if (d0.toInt() != d1.toInt()) {
            mProcessBar.setCurrentValues(0f)
        } else {
            mProcessBar.setCurrentValues(steps.toFloat())
        }
    }

    private fun resetLayoutColor() {
        mDailyHeartRateLayout.setBackgroundColor(resources.getColor(R.color.white))
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            setSteps()
        }
    }

    override fun onResume() {
        super.onResume()
        resetLayoutColor()
    }

    companion object {
        private val TAG = "HomeFragment"
    }
}