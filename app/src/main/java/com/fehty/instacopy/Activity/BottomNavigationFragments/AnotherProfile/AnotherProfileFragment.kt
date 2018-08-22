package com.fehty.instacopy.Activity.BottomNavigationFragments.AnotherProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.Activity.Data.UserProfileData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_another_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class AnotherProfileFragment(var userId: Int) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_another_profile, container, false)
    }

    var userName: String? = null
    var galleryImagePath: String? = null
    val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.toolbarTakePhoto.visibility = View.GONE
        activity!!.toolbarOpenGallery.visibility = View.GONE
        activity!!.toolbarUserSettings.visibility = View.GONE

        Picasso.get().load("http://178.128.239.249/user/avatar?userId=$userId").memoryPolicy(MemoryPolicy.NO_CACHE).into(userAvatar)
        getUserNameById()
        getUserMessagesById()
    }

    private fun getUserNameById() {
        MyApplication().retrofit.getUserNameEmailByUserId(userId!!).enqueue(object : Callback<UserProfileData> {
            override fun onFailure(call: Call<UserProfileData>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<UserProfileData>?, response: Response<UserProfileData>?) {
                mainUserName.text = response!!.body()!!.name
                email.text = response.body()!!.email
            }
        })
    }

    fun getUserMessagesById() {
        val listOfPhotos = mutableListOf<MessageData>()
        val adapter = AnotherProfileAdapter(this@AnotherProfileFragment, listOfPhotos)
        val layoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        MyApplication().retrofit.getUserMessagesById(userId!!).enqueue(object : Callback<List<MessageData>> {
            override fun onFailure(call: Call<List<MessageData>>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<List<MessageData>>?, response: Response<List<MessageData>>?) {
                response!!.body()!!.forEach {
                    if (it.filename != null && it.userId != null) {
                        if (it.comments != null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, it.comments, it.userId, it.likes))
                        } else if (it.comments == null) {
                            listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, null, it.userId, it.likes))
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                activity!!.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onPause() {
        super.onPause()
        activity!!.toolbarUserSettings.visibility = View.INVISIBLE
    }
}
