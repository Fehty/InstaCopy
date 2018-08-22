package com.fehty.instacopy.Activity.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fehty.instacopy.Activity.BottomNavigationFragments.GalleryFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.Home.HomeFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.MyProfile.MyProfileFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.TakePhotoFragment
import com.fehty.instacopy.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()
        setSupportActionBar(mainActivityToolbar)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainList -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.container, HomeFragment()).commit()
                R.id.myProfile -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.container, MyProfileFragment()).commit()
            }
          true
        }

        toolbarTakePhoto.setOnClickListener { supportFragmentManager.beginTransaction().replace(R.id.container, TakePhotoFragment()).commit() }
        toolbarOpenGallery.setOnClickListener { supportFragmentManager.beginTransaction().replace(R.id.container, GalleryFragment()).commit() }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        onBackPressed()
        bottomNavigationView.visibility = View.VISIBLE
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        bottomNavigationView.visibility = View.VISIBLE
    }
}

