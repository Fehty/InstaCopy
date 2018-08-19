package com.fehty.instacopy.Activity.AlertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.UserProfileFragment
import com.fehty.instacopy.Activity.Data.MessageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class DeleteMessageFragmentDialog(var userProfileFragment: UserProfileFragment, var messageId: Int) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder
                .setTitle("Confirmation")
                .setMessage("Are you sure?")
                .setPositiveButton("Ok") { p0, p1 ->
                    MyApplication().retrofit.deleteMessage(messageId).enqueue(object : Callback<MessageData> {
                        override fun onResponse(call: Call<MessageData>?, response: Response<MessageData>?) = Unit
                        override fun onFailure(call: Call<MessageData>?, t: Throwable?) {
                            userProfileFragment.getUserMessagesByToken()
                        }
                    })
                }
                .setNegativeButton("No") { p0, p1 -> }
                .create()
        return builder.create()
    }
}