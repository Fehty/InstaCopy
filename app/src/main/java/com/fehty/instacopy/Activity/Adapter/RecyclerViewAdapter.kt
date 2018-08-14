package com.fehty.instacopy.Activity.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.fehty.instacopy.R
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(var list: MutableList<Any>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView = view.findViewById<ImageView>(R.id.imageView)

        fun bind(imageName: Any) {
            Picasso.get().load("http://138.197.150.20/images/$imageName").into(imageView)
        }
    }
}