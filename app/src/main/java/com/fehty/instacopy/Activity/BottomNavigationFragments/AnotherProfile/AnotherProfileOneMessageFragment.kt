package com.fehty.instacopy.Activity.BottomNavigationFragments.AnotherProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fehty.instacopy.Activity.Activity.MainActivity
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.Home.HomeOneMessageAdapter
import com.fehty.instacopy.Activity.Data.AddCommentData
import com.fehty.instacopy.Activity.Data.CommentData
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.Activity.Data.UsersWhoLikedPostData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_one_message.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("ValidFragment")
class AnotherProfileOneMessageFragment(var messageData: MessageData) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_one_message, container, false)
    }

    val comments = mutableListOf<CommentData>()
    val adapter = HomeOneMessageAdapter(comments)
    val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load("http://178.128.239.249/${messageData.filename}").into(photo)

        getMessageData()

        userId.text = messageData.userId.toString()
        photoDescription.text = messageData.text

        favourite.setOnClickListener {
            MyApplication().retrofit.addLike(messageData.id, token).enqueue(object : Callback<List<UsersWhoLikedPostData>> {
                override fun onResponse(call: Call<List<UsersWhoLikedPostData>>?, response: Response<List<UsersWhoLikedPostData>>?) {
                    getMessageData()
                }

                override fun onFailure(call: Call<List<UsersWhoLikedPostData>>?, t: Throwable?) {
                    getMessageData()
                }
            })
        }

        val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!

        addComment.setOnClickListener {
            try {
                if (comment.text.isNotEmpty()) {
                    MyApplication().retrofit.addComment(messageData.id, AddCommentData(comment.text.toString().trim()), token).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>?, response: Response<String>?) = Unit
                        override fun onFailure(call: Call<String>?, t: Throwable?) {
                            getMessageData()
                        }
                    })
                    comment.text.clear()
                } else Toast.makeText(activity, "Empty field", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(activity, "Empty field", Toast.LENGTH_SHORT).show()
            }
        }

        activity!!.bottomNavigationView.visibility = View.GONE

        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun getMessageData() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        MyApplication().retrofit.getMessageById(messageData.id).enqueue(object : Callback<MessageData> {
            override fun onFailure(call: Call<MessageData>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<MessageData>?, response: Response<MessageData>?) {
                comments.clear()
                response!!.body()!!.comments!!.forEach { comments.add(CommentData(it.user, it.text)) }
                adapter.notifyDataSetChanged()
                activity!!.progressBar.visibility = View.GONE
                usersWhoLikedPost.text = response.body()!!.likes.size.toString()
                usersWhoCommentedPost.text = response.body()!!.comments!!.size.toString()
            }
        })
    }
}
