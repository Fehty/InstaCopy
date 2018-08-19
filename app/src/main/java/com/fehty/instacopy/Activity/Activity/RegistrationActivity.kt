package com.fehty.instacopy.Activity.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.RegistrationData
import com.fehty.instacopy.R
import kotlinx.android.synthetic.main.activity_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registrationBackButton.setOnClickListener { switchToLoginActivity() }

        registrationConfirmationButton.setOnClickListener {
            if (registrationName.text.length < 2) registrationName.error = "Incorrect"
            if (registrationEmail.text.length < 9) registrationEmail.error = "Incorrect"
            if (registrationPassword.text.length < 7) registrationPassword.error = "Incorrect"
            else {
                val registrationJsonData = RegistrationData(registrationName.text.toString(), registrationEmail.text.toString(), registrationPassword.text.toString(), registrationPassword.text.toString())
                MyApplication().retrofit.register(registrationJsonData).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>?, response: Response<String>?) = Unit
                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        switchToLoginActivity()
                    }
                })
            }
        }
    }

    private fun switchToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}