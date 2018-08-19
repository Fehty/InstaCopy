package com.fehty.instacopy.Activity.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

    private val realm = Realm.getDefaultInstance()
    private val tokenRealm = TokenRealm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                    override fun onFailure(call: Call<UserTokenData>?, t: Throwable?) = Unit
                    override fun onResponse(call: Call<UserTokenData>?, response: Response<UserTokenData>?) {
                        realm.executeTransaction {
                            tokenRealm.userToken = response!!.body()!!.value
                            realm.insertOrUpdate(tokenRealm)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
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