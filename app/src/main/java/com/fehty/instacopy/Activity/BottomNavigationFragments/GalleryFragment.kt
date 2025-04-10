package com.fehty.instacopy.Activity.BottomNavigationFragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fehty.instacopy.Activity.Application.MyApplication
import com.fehty.instacopy.Activity.BottomNavigationFragments.Home.HomeFragment
import com.fehty.instacopy.Activity.Realm.TokenRealm
import com.fehty.instacopy.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GalleryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        checkStoragePermissionToOpenGallery()
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    fun checkStoragePermissionToOpenGallery() {
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        } else openGalleryIntent()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 ->
                if (activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    openGalleryIntent()
                else {
                    Log.e("#*#*#*", "!Granted!!!")
                    fragmentManager!!.beginTransaction().replace(R.id.container, HomeFragment()).commit()
                }
        }
    }

    private var galleryImagePath: String? = null

    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (data != null) {
                val pickedImage = data.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity!!.contentResolver.query(pickedImage, filePath, null, null, null)
                cursor!!.moveToFirst()
                val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))
//                chosenPhoto.setImageURI(Uri.parse(imagePath))
                galleryImagePath = imagePath
                uploadDataToServer()
                cursor.close()
            } else fragmentManager!!.beginTransaction().replace(R.id.container, HomeFragment()).commit()
        }
    }

    private fun uploadDataToServer() {
        activity!!.progressBar.visibility = View.VISIBLE

        val message = RequestBody.create(MediaType.parse("text/plain"), "")

        val file = File(galleryImagePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val fileData = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val tokenRealm = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken
        val token = RequestBody.create(MediaType.parse("text/plain"), tokenRealm!!)

        MyApplication().retrofit.uploadMessage(message, fileData, token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) = Unit
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                fragmentManager?.beginTransaction()?.replace(R.id.container, HomeFragment())?.commit()
            }
        })
    }
}
