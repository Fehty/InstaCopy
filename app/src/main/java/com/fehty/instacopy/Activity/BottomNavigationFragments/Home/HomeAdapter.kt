package com.fehty.instacopy.Activity.BottomNavigationFragments.Home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.AnotherProfile.AnotherProfileFragment
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.Activity.Data.UsersWhoLikedPostData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeAdapter(var homeFragment: HomeFragment, var list: MutableList<MessageData>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_home, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val userId = view.findViewById<TextView>(R.id.userId)
        private val photoDescription = view.findViewById<TextView>(R.id.photoDescription)
        private val photo = view.findViewById<ImageView>(R.id.photo)
        private val addLikeButton = view.findViewById<ImageButton>(R.id.addLikeButton)
        private val usersWhoLikedPostData = view.findViewById<TextView>(R.id.numberOfUsersWhoLikedPost)
        private val hideShowComments = view.findViewById<ImageButton>(R.id.hideShowComments)
        private val commentsNumber = view.findViewById<TextView>(R.id.commentsNumber)

        fun bind(messageData: MessageData) {

            val token = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!

            userId.text = messageData.userId.toString()

            userId.setOnClickListener {
                homeFragment.fragmentManager!!.beginTransaction().addToBackStack(null)
                        .replace(R.id.container, AnotherProfileFragment(messageData.userId))
                        .commit()
            }

            photoDescription.text = messageData.text
            Picasso.get().load("http://178.128.239.249/${messageData.filename}").into(photo)

            photo.setOnClickListener {
                homeFragment.fragmentManager!!.beginTransaction().addToBackStack(null)
                        .replace(R.id.container, HomeOneMessageFragment(messageData)).commit()
            }

            addLikeButton.setOnClickListener {
                MyApplication().retrofit.addLike(messageData.id, token).enqueue(object : Callback<List<UsersWhoLikedPostData>> {
                    override fun onResponse(call: Call<List<UsersWhoLikedPostData>>?, response: Response<List<UsersWhoLikedPostData>>?) {
                        homeFragment.getDataFromServer()
                    }

                    override fun onFailure(call: Call<List<UsersWhoLikedPostData>>?, t: Throwable?) {
                        homeFragment.getDataFromServer()
                    }
                })
            }

            usersWhoLikedPostData.text = messageData.likes.size.toString()

            commentsNumber.text = messageData.comments!!.size.toString()

            hideShowComments.setOnClickListener {
                homeFragment.fragmentManager!!.beginTransaction().addToBackStack(null)
                        .replace(R.id.container, HomeOneMessageFragment(messageData)).commit()
            }
        }
    }
}