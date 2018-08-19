package com.fehty.instacopy.Activity.AlertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.UserProfileFragment
import com.fehty.instacopy.Activity.Data.MessageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class EditTextFragmentDialog(var userProfileFragment: UserProfileFragment, var messageData: MessageData) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val editText = EditText(activity)
        editText.setText(messageData.text)
        builder
                .setTitle("Confirmation")
                .setMessage(messageData.text)
                .setView(editText)
                .setPositiveButton("Ok") { p0, p1 ->
                    MyApplication().retrofit.editMessage(messageData.id, editText.text.toString()).enqueue(object : Callback<MessageData> {
                        override fun onFailure(call: Call<MessageData>?, t: Throwable?) = Unit
                        override fun onResponse(call: Call<MessageData>?, response: Response<MessageData>?) {
                            userProfileFragment.getUserMessagesByToken()
                        }
                    })
                }
                .setNegativeButton("No") { p0, p1 -> }
                .create()
        return builder.create()
    }
}