package com.zjut.wristband.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import com.zjut.wristband.R

class HomeActivity : AppCompatActivity() {
    companion object {
        val TAG = "HomeActivity"
    }

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mMenuButton: Button
    private lateinit var mTitleTextView: TextView

    private lateinit var mSpaceNavigationView: SpaceNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
        mMenuButton.visibility = View.VISIBLE
        mMenuButton.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
        mSpaceNavigationView.initWithSaveInstanceState(savedInstanceState)

        mSpaceNavigationView.addSpaceItem(SpaceItem("test", R.drawable.ic_wristband))
        mSpaceNavigationView.addSpaceItem(SpaceItem("test", R.drawable.ic_wristband))

        mSpaceNavigationView.showIconOnly()
        mSpaceNavigationView.setCentreButtonIconColorFilterEnabled(false)
        mSpaceNavigationView.setActiveCentreButtonBackgroundColor(resources.getColor(R.color.white))


        mSpaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {

            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {

            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {

            }

        })
    }


}
