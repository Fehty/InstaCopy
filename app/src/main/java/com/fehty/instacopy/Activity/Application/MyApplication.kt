package com.fehty.instacopy.Activity.Application

import android.app.Application
import com.fehty.instacopy.Activity.API.ApiMethods
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

    val retrofit = Retrofit.Builder()
            .baseUrl("http://178.128.239.249")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMethods::class.java)!!
}