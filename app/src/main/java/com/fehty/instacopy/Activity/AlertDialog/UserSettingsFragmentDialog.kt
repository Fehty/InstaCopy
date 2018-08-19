package com.fehty.instacopy.Activity.AlertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.UserProfileFragment
import com.fehty.instacopy.Activity.Data.ChangeUserNameData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class UserSettingsFragmentDialog(var userProfileFragment: UserProfileFragment, var userName: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!
        val builder = AlertDialog.Builder(activity)
        val userNameEditText = EditText(activity)
        userNameEditText.setText(userName)
        builder
                .setTitle("Confirmation")
                .setMessage("Are you sure?")
                .setView(userNameEditText)
                .setPositiveButton("Ok") { p0, p1 ->
                    if (userNameEditText.text.toString() != userName) {
                        MyApplication().retrofit.changeUserName(token, ChangeUserNameData(userNameEditText.text.toString())).enqueue(object : Callback<Void> {
                            override fun onFailure(call: Call<Void>?, t: Throwable?) = Unit
                            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                                userProfileFragment.getUserNameEmailByToken()
                            }
                        })
                    }
                }
                .setNegativeButton("No") { p0, p1 ->
                    Log.e("*#*#*", "No")
                }
                .create()
        return builder.create()
    }
}