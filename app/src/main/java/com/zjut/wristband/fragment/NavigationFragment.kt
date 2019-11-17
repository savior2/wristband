package com.zjut.wristband.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar


class NavigationFragment : Fragment() {

    private lateinit var mSidTextView: TextView
    private lateinit var mNameTextView: TextView
    private lateinit var mSexTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        mSidTextView = view.findViewById(R.id.sid_text_view)
        mNameTextView = view.findViewById(R.id.name_text_view)
        mSexTextView = view.findViewById(R.id.sex_text_view)
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSidTextView.text = mSidTextView.text.toString() + MemoryVar.sid
        mNameTextView.text = mNameTextView.text.toString() + MemoryVar.name
        mSexTextView.text = mSexTextView.text.toString() + MemoryVar.sex
    }

    companion object {
        val TAG = "NavigationFragment"
    }
}
