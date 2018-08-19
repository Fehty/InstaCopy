package com.fehty.instacopy.Activity.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fehty.instacopy.Activity.BottomNavigationFragments.GalleryFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.MainListFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.TakePhotoFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.UserProfileFragment
import com.fehty.instacopy.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, MainListFragment()).commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainList -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.container, MainListFragment()).commit()
                R.id.takePhoto -> supportFragmentManager.beginTransaction().replace(R.id.container, TakePhotoFragment()).commit()
                R.id.gallery -> supportFragmentManager.beginTransaction().replace(R.id.container, GalleryFragment()).commit()
                R.id.myProfile -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.container, UserProfileFragment()).commit()
            }
            true
        }
    }
}

