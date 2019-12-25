package com.zjut.wristband.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband.R

class AboutActivity : AppCompatActivity() {

    private lateinit var mTitleTextView: TextView
    private lateinit var mBackButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        mTitleTextView = findViewById(R.id.title_text_view)
        mTitleTextView.text = "关于浙工健行"
        mBackButton = findViewById(R.id.back_button)
        mBackButton.setOnClickListener { this.finish() }
        mBackButton.visibility = View.VISIBLE
    }

    companion object {
        private val TAG = "AboutActivity"
    }
}