package com.zjut.wristband.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.zjut.wristband.R
import com.zjut.wristband.fragment.DeviceConnectFragment
import com.zjut.wristband.fragment.HomeFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mMenuButton: Button
    private lateinit var mTitleTextView: TextView

    private lateinit var mSpaceNavigationView: SpaceNavigationView

    private lateinit var mHomeFragment: HomeFragment
    private lateinit var mDeviceConnectFragment: DeviceConnectFragment
    private lateinit var mCurrentFragment: Fragment
    private var mCurrentIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initFragments()
        initial()
        initComponent(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSpaceNavigationView.onSaveInstanceState(outState)
    }

    private fun initial() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mMenuButton = findViewById(R.id.menu_button)
        mTitleTextView = findViewById(R.id.title_text_view)
        mSpaceNavigationView = findViewById(R.id.space_navigation_view)
    }

    private fun initComponent(savedInstanceState: Bundle?) {
        mTitleTextView.text = "首页"
        mMenuButton.visibility = View.VISIBLE
        mMenuButton.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }

        mSpaceNavigationView.initWithSaveInstanceState(savedInstanceState)
        mSpaceNavigationView.addSpaceItem(SpaceItem("首页", R.drawable.ic_home))
        mSpaceNavigationView.addSpaceItem(SpaceItem("手环", R.drawable.ic_wristband))
        mSpaceNavigationView.showIconOnly()
        mSpaceNavigationView.setCentreButtonIconColorFilterEnabled(false)
        mSpaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {

            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {

            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {
                when (itemIndex) {
                    INDEX_HOME -> {
                        mTitleTextView.text = "首页"
                        mCurrentIndex = INDEX_HOME
                        replaceFragment(mHomeFragment)
                    }
                    INDEX_DEVICE -> {
                        mTitleTextView.text = "设备绑定"
                        mCurrentIndex = INDEX_DEVICE
                        replaceFragment(mDeviceConnectFragment)
                    }
                }
            }
        })

    }

    private fun initFragments() {
        val transaction = supportFragmentManager.beginTransaction()
        mHomeFragment = HomeFragment()
        mDeviceConnectFragment = DeviceConnectFragment()
        mCurrentFragment = mHomeFragment
        transaction.add(R.id.fragment_container, mCurrentFragment)
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(mCurrentFragment)
        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.fragment_container, fragment)
        }
        mCurrentFragment = fragment
        transaction.commit()
    }


    companion object {
        private val TAG = "HomeActivity"
        private val INDEX_HOME = 0
        private val INDEX_DEVICE = 1
    }

}
