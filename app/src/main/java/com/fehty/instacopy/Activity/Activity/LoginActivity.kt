package com.fehty.instacopy.Activity.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.LoginData
import com.fehty.instacopy.Activity.Data.UserTokenData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val realm = Realm.getDefaultInstance()
        val tokenRealm = TokenRealm()

        if (realm.where(TokenRealm::class.java).findFirst() != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        loginConfirmationButton.setOnClickListener {
            if (loginEmail.text.length < 9) loginEmail.error = "Incorrect"
            if (loginPassword.text.length < 7) loginPassword.error = "Incorrect"
            else {
                val loginJsonData = LoginData(loginEmail.text.toString(), loginPassword.text.toString())
                MyApplication().retrofit.login(loginJsonData).enqueue(object : Callback<UserTokenData> {
                    override fun onFailure(call: Call<UserTokenData>?, t: Throwable?) {
                        Toast.makeText(this@LoginActivity, "Wrong Data", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<UserTokenData>?, response: Response<UserTokenData>?) {
                        Log.e("#**#*", response!!.body()!!.value)
                        if (response?.body()?.value != null) {
                            realm.executeTransaction {
                                realm.where(TokenRealm::class.java).findAll().deleteAllFromRealm()
                                tokenRealm.userToken = response!!.body()!!.value
                                realm.insertOrUpdate(tokenRealm)
                            }
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Wrong Data", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        loginRegistrationButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}