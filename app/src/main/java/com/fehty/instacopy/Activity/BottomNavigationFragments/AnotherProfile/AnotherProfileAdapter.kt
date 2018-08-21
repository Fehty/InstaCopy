package com.fehty.instacopy.Activity.BottomNavigationFragments.AnotherProfile

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso

class AnotherProfileAdapter(var anotherProfileFragment: AnotherProfileFragment, var list: MutableList<MessageData>) : RecyclerView.Adapter<AnotherProfileAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_another_profile, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val photo = view.findViewById<ImageView>(R.id.photo)

        fun bind(messageData: MessageData) {
            Picasso.get().load("http://178.128.239.249/${messageData.filename}").into(photo)
            photo.setOnClickListener {
                anotherProfileFragment.fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.container, AnotherProfileOneMessageFragment(messageData)).commit()
            }
        }
    }
}