package com.fehty.instacopy.Activity.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.RegistrationData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val realm = Realm.getDefaultInstance()
        val tokenRealm = TokenRealm()

        registrationBackButton.setOnClickListener { switchToLoginActivity() }

        registrationConfirmationButton.setOnClickListener {
            if (registrationName.text.length < 2) registrationName.error = "Incorrect"
            if (registrationEmail.text.length < 9) registrationEmail.error = "Incorrect"
            if (registrationPassword.text.length < 7) registrationPassword.error = "Incorrect"
            else {
                val registrationJsonData = RegistrationData(registrationName.text.toString(), registrationEmail.text.toString(), registrationPassword.text.toString(), registrationPassword.text.toString())
                MyApplication().retrofit.register(registrationJsonData).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
                        Toast.makeText(this@RegistrationActivity, "Wrong Data", Toast.LENGTH_SHORT).show()
                        Log.e("#*#**#", response.toString())
                    }

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