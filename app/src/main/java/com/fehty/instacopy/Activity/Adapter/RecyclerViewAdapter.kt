package com.fehty.instacopy.Activity.Adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fehty.instacopy.Activity.AlertDialog.DeleteMessageFragmentDialog
import com.fehty.instacopy.Activity.AlertDialog.EditTextFragmentDialog
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.MainListFragment
import com.fehty.instacopy.Activity.BottomNavigationFragments.UserProfileFragment
import com.fehty.instacopy.Activity.Data.AddCommentData
import com.fehty.instacopy.Activity.Data.CommentData
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.Activity.Data.UsersWhoLikedPostData
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecyclerViewAdapter(
        var mainListFragment: MainListFragment,
        var photoList: MutableList<MessageData>,
        var inUserProfileFragment: Boolean,
        var userProfileFragment: UserProfileFragment? = null
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val userId = view.findViewById<TextView>(R.id.userId)
        private val photoDescription = view.findViewById<TextView>(R.id.photoDescription)
        private val addLikeButton = view.findViewById<ImageButton>(R.id.addLikeButton)
        private val usersWhoLikedPostData = view.findViewById<TextView>(R.id.numberOfUsersWhoLikedPost)
        private val photo = view.findViewById<ImageView>(R.id.photo)
        private val addCommentText = view.findViewById<EditText>(R.id.addCommentText)
        private val addCommentButton = view.findViewById<Button>(R.id.addCommentButton)
        private val commentsRecyclerView = view.findViewById<RecyclerView>(R.id.commentsRecyclerView)
        private val hideShowComments = view.findViewById<ImageButton>(R.id.hideShowComments)
        private val commentsNumber = view.findViewById<TextView>(R.id.commentsNumber)
        private val navigationMenu = view.findViewById<ImageButton>(R.id.navigationMenu)

        fun bind(messageData: MessageData) {

            addCommentButton.setOnClickListener {
                MyApplication().retrofit.addComment(messageData.id, AddCommentData(addCommentText.text.toString().trim()), Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>?, response: Response<String>?) = Unit
                    override fun onFailure(call: Call<String>?, t: Throwable?) = Unit
                })
                addCommentText.text.clear()
                if (inUserProfileFragment) userProfileFragment!!.getUserMessagesByToken()
                else mainListFragment.getDataFromServer()
            }

            addLikeButton.setOnClickListener {
                val tokenR = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken!!
                MyApplication().retrofit.addLike(messageData.id, tokenR).enqueue(object : Callback<List<UsersWhoLikedPostData>> {
                    override fun onResponse(call: Call<List<UsersWhoLikedPostData>>?, response: Response<List<UsersWhoLikedPostData>>?) = Unit
                    override fun onFailure(call: Call<List<UsersWhoLikedPostData>>?, t: Throwable?) = Unit
                })
                if (inUserProfileFragment) userProfileFragment!!.getUserMessagesByToken()
                else mainListFragment.getDataFromServer()
            }

            navigationMenu.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_navigation, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.deleteMessage) DeleteMessageFragmentDialog(userProfileFragment!!, messageData.id).show(userProfileFragment!!.fragmentManager, "4")
                    else if (it.itemId == R.id.changeMessageText) EditTextFragmentDialog(userProfileFragment!!, messageData).show(userProfileFragment!!.fragmentManager, "2")
                    true
                }
                popupMenu.show()
            }

            var isCommentOpened = false
            hideShowComments.setOnClickListener {
                if (!isCommentOpened) {
                    isCommentOpened = true
                    commentsRecyclerView.visibility = View.VISIBLE
                    addCommentButton.visibility = View.VISIBLE
                    addCommentText.visibility = View.VISIBLE
                } else if (isCommentOpened) {
                    isCommentOpened = false
                    commentsRecyclerView.visibility = View.GONE
                    addCommentButton.visibility = View.GONE
                    addCommentText.visibility = View.GONE
                }
            }

            userId.setOnClickListener {
                mainListFragment.fragmentManager!!.beginTransaction().addToBackStack(null)
                        .replace(R.id.container, UserProfileFragment(false, messageData.userId))
                        .commit()
            }

            if (messageData.comments != null) {
                val commentList = mutableListOf<CommentData>()
                messageData.comments.forEach { commentList.add(CommentData(it.user, it.text)) }
                val adapter = CommentsRecyclerViewAdapter(commentList)
                commentsRecyclerView.layoutManager = LinearLayoutManager(mainListFragment.context)
                commentsRecyclerView.adapter = adapter
            }

            Picasso.get().load("http://178.128.239.249/${messageData.filename}").into(photo)
            if (inUserProfileFragment == true) navigationMenu.visibility = View.VISIBLE
            if (photoDescription.text != null) photoDescription.text = messageData.text
            userId!!.text = messageData.userId.toString()
            usersWhoLikedPostData.text = (messageData.likes.size).toString()
            commentsNumber.text = messageData.comments!!.size.toString()
        }
    }

    inner class CommentsRecyclerViewAdapter(private var commentList: MutableList<CommentData>) : RecyclerView.Adapter<RecyclerViewAdapter.CommentsRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.template_comment, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(commentList[position])
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val userId = view.findViewById<TextView>(R.id.userId)
            private val comment = view.findViewById<TextView>(R.id.comment)

            fun bind(commentData: CommentData) {
                userId.text = commentData.user.toString()
                comment.text = commentData.text
            }
        }
    }
}