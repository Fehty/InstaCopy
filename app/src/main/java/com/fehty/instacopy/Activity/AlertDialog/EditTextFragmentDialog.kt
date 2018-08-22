package com.fehty.instacopy.Activity.AlertDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.widget.EditText
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.MyProfile.MyProfileOneMessageFragment
import com.fehty.instacopy.Activity.Data.MessageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class EditTextFragmentDialog(var myProfileOneMessageFragment: MyProfileOneMessageFragment, var messageData: MessageData, var photoDescription: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val editText = EditText(activity)
        editText.setText(photoDescription)
        editText.gravity = Gravity.CENTER
        builder
                .setTitle("Change message text")
                .setView(editText)
                .setPositiveButton("Ok") { p0, p1 ->
                    MyApplication().retrofit.editMessage(messageData.id, editText.text.toString()).enqueue(object : Callback<MessageData> {
                        override fun onFailure(call: Call<MessageData>?, t: Throwable?){
                            myProfileOneMessageFragment.getMessageData()
                        }
                        override fun onResponse(call: Call<MessageData>?, response: Response<MessageData>?) {
                            myProfileOneMessageFragment.getMessageData()
                        }
                    })
                }
                .setNegativeButton("No") { p0, p1 -> }
                .create()
        return builder.create()
    }
}