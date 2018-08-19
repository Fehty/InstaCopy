package com.fehty.instacopy.Activity.BottomNavigationFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fehty.instacopy.Activity.Adapter.RecyclerViewAdapter
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.Data.MessageData
import com.fehty.instacopy.R
import kotlinx.android.synthetic.main.fragment_main_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromServer()
    }

    fun getDataFromServer() {
        MyApplication().retrofit.getAllPhotos().enqueue(object : Callback<List<MessageData>> {
            override fun onFailure(call: Call<List<MessageData>>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<List<MessageData>>?, response: Response<List<MessageData>>?) {
                val listOfPhotos = mutableListOf<MessageData>()
                response!!.body()!!.forEach {
                    if (it.filename != null && it.userId != null) {
                        if (it.comments != null) listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, it.comments, it.userId, it.likes))
                        else if (it.comments == null) listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, null, it.userId, it.likes))
                    }
                }
                val adapter = RecyclerViewAdapter(this@MainListFragment, listOfPhotos, false)
                val layoutManager = LinearLayoutManager(activity)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        })
    }
}
