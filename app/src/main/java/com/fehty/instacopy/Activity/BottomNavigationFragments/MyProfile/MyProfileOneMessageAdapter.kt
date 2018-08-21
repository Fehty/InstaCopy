package com.fehty.instacopy.Activity.BottomNavigationFragments.MyProfile

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fehty.instacopy.Activity.Data.CommentData
import com.fehty.instacopy.R

class MyProfileOneMessageAdapter(var commentList: MutableList<CommentData>) : RecyclerView.Adapter<MyProfileOneMessageAdapter.ViewHolder>() {
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

        val userId = view.findViewById<TextView>(R.id.userId)
        val comment = view.findViewById<TextView>(R.id.comment)

        fun bind(commentData: CommentData) {
            userId.text = commentData.user.toString()
            comment.text = commentData.text
        }
    }
}