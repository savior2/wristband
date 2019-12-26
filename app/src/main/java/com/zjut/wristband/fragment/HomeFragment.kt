package com.zjut.wristband.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shinelw.library.ColorArcProgressBar
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar
import com.zjut.wristband.util.SharedPreFile
import com.zjut.wristband.util.SharedPreKey
import com.zjut.wristband.util.SharedPreUtil
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var mProcessBar: ColorArcProgressBar
    private lateinit var mSyncStepsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mProcessBar = view.findViewById(R.id.process_bar)
        mSyncStepsButton = view.findViewById(R.id.sync_steps_button)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSteps()
        mSyncStepsButton.setOnClickListener {
            if (MemoryVar.device != null) {
                setSteps()
                Toast.makeText(activity, "手环数据已经同步！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "请先连接手环！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            setSteps()
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
        if (d0 != d1) {
            mProcessBar.setCurrentValues(0f)
        } else {
            mProcessBar.setCurrentValues(steps.toFloat())
        }
    }

    companion object {
        private val TAG = "HomeFragment"
    }
}