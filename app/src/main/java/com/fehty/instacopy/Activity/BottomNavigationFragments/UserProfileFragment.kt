package com.fehty.instacopy.Activity.BottomNavigationFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fehty.instacopy.Activity.Adapter.RecyclerViewAdapter
import com.fehty.instacopy.Activity.AlertDialog.UserSettingsFragmentDialog
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.Activity.Data.UserProfileData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@SuppressLint("ValidFragment")
class UserProfileFragment(var myProfile: Boolean = true, var userId: Int? = null) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    var userName: String? = null
    var galleryImagePath: String? = null
    val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("*#*#*", token)

        if (myProfile == true) {
            activity!!.toolbarUserSettings.visibility = View.VISIBLE
            Picasso.get().load("http://178.128.239.249/user/avatar?token=$token").into(userAvatar)
            getUserNameEmailByToken()
            getUserMessagesByToken()
            activity!!.toolbarUserSettings.setOnClickListener { showPopupMenu(it) }

        } else if (myProfile == false) {
            Picasso.get().load("http://178.128.239.249/user/avatar?userId=$userId").into(userAvatar)
            getUserNameById()
            getUserMessagesById()
        }
    }

    fun getUserNameEmailByToken() {
        MyApplication().retrofit.getUserNameEmailByToken(token).enqueue(object : Callback<UserProfileData> {
            override fun onFailure(call: Call<UserProfileData>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<UserProfileData>?, response: Response<UserProfileData>?) {
                firstName.text = response!!.body()!!.name
                userName = response.body()!!.name
                email.text = response.body()!!.email
            }
        })
        Picasso.get().load("http://178.128.239.249/user/avatar?token=$token").into(userAvatar)
    }

    fun getUserMessagesByToken() {
        MyApplication().retrofit.getUserMessagesByToken(token).enqueue(object : Callback<List<MessageData>> {
            override fun onFailure(call: Call<List<MessageData>>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<List<MessageData>>?, response: Response<List<MessageData>>?) {
                val listOfPhotos = mutableListOf<MessageData>()
                response!!.body()!!.forEach {
                    if (it.filename != null && it.userId != null) {
                        if (it.comments != null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, it.comments, it.userId, it.likes))
                        } else if (it.comments == null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, null, it.userId, it.likes))
                        }
                    }
                }
                val adapter = RecyclerViewAdapter(MainListFragment(), listOfPhotos, true, this@UserProfileFragment)
                val layoutManager = GridLayoutManager(activity, 2)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        })
    }

    private fun getUserNameById() {
        MyApplication().retrofit.getUserNameEmailByUserId(userId!!).enqueue(object : Callback<UserProfileData> {
            override fun onFailure(call: Call<UserProfileData>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<UserProfileData>?, response: Response<UserProfileData>?) {
                firstName.text = response!!.body()!!.name
                email.text = response.body()!!.email
            }
        })
    }

    private fun getUserMessagesById() {
        MyApplication().retrofit.getUserMessagesById(userId!!).enqueue(object : Callback<List<MessageData>> {
            override fun onFailure(call: Call<List<MessageData>>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<List<MessageData>>?, response: Response<List<MessageData>>?) {
                val listOfPhotos = mutableListOf<MessageData>()
                response!!.body()!!.forEach {
                    if (it.filename != null && it.userId != null) {
                        if (it.comments != null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, it.comments, it.userId, it.likes))
                        } else if (it.comments == null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, null, it.userId, it.likes))
                        }
                    }
                }
                val adapter = RecyclerViewAdapter(MainListFragment(), listOfPhotos, false)
                val layoutManager = GridLayoutManager(activity, 2)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        })
    }

    fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this@UserProfileFragment.context!!, view)
        popupMenu.menuInflater.inflate(R.menu.menu_user_profile_toolbar, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            if (it.itemId == R.id.changeAvatar) {
                openGalleryIntent()
            } else if (it.itemId == R.id.changeName) {
                UserSettingsFragmentDialog(this@UserProfileFragment, userName!!).show(fragmentManager, "0")
            }
            true
        }
        popupMenu.show()
    }

    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val pickedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity!!.contentResolver.query(pickedImage, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))
            galleryImagePath = imagePath
            cursor.close()

            val tokenR = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!
            val token = RequestBody.create(MediaType.parse("text/plain"), tokenR)

            val file = File(galleryImagePath)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            val fileData = MultipartBody.Part.createFormData("file", file.name, requestFile)
            MyApplication().retrofit.setUserAvatar(token, fileData).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) = Unit
                override fun onFailure(call: Call<String>?, t: Throwable?) = Unit
            })
        }
    }

    override fun onPause() {
        super.onPause()
        activity!!.toolbarUserSettings.visibility = View.INVISIBLE
    }

}