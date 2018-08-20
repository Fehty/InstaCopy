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
import kotlinx.android.synthetic.main.activity_main.*
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
        activity!!.toolbarUserSettings.visibility = View.GONE
        activity!!.toolbarTakePhoto.visibility = View.VISIBLE
        activity!!.toolbarOpenGallery.visibility = View.VISIBLE
        getDataFromServer()
    }

    fun getDataFromServer() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        val listOfPhotos = mutableListOf<MessageData>()
        val adapter = RecyclerViewAdapter(this@MainListFragment, listOfPhotos, false)
        recyclerView.adapter = adapter
        MyApplication().retrofit.getAllPhotos().enqueue(object : Callback<List<MessageData>> {
            override fun onFailure(call: Call<List<MessageData>>?, t: Throwable?) = Unit
            override fun onResponse(call: Call<List<MessageData>>?, response: Response<List<MessageData>>?) {
                response!!.body()!!.forEach {
                    if (it.filename != null && it.userId != null) {
                        if (it.comments != null) listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, it.comments, it.userId, it.likes))
                        else if (it.comments == null) listOfPhotos.add(MessageData(it.id, it.text, it.filename, it.date, null, it.userId, it.likes))
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}
