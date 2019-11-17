package com.zjut.wristband.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.zjut.wristband.R
import com.zjut.wristband.util.MemoryVar

class HomeActivity : AppCompatActivity() {
    companion object {
        val TAG = "HomeActivity"
    }

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mMenuButton: Button
    private lateinit var mTitleTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initial()
        mMenuButton.visibility = View.VISIBLE
        mMenuButton.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun initial() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mMenuButton = findViewById(R.id.menu_button)
        mTitleTextView = findViewById(R.id.title_text_view)

    }


}
