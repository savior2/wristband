package com.zjut.wristband.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shinelw.library.ColorArcProgressBar
import com.zjut.wristband.R

class HomeFragment : Fragment() {

    private lateinit var mProcessBar: ColorArcProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mProcessBar = view.findViewById(R.id.process_bar)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mProcessBar.setCurrentValues(200f)
    }
}