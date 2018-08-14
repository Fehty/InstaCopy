package com.fehty.instacopy.Activity.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fehty.instacopy.Activity.API.MyData
import com.fehty.instacopy.Activity.API.MyInterface
import com.fehty.instacopy.Activity.Adapter.RecyclerViewAdapter
import com.fehty.instacopy.R
import kotlinx.android.synthetic.main.fragment_list_of_photos.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ListOfPhotos : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_of_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromServer()

        takePhotoButton.setOnClickListener { openGalleryIntent() }
    }

    private fun getDataFromServer() {

        val retrofit = Retrofit.Builder()
                .baseUrl("http://138.197.150.20")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyInterface::class.java)

        retrofit.getData().enqueue(object : Callback<List<MyData>> {
            override fun onResponse(call: Call<List<MyData>>?, response: Response<List<MyData>>?) {

                val listOfPhotos = mutableListOf<Any>()
                response!!.body()!!.forEach { if (it.filename != null) listOfPhotos.add(it.filename) }

                val adapter = RecyclerViewAdapter(listOfPhotos)

                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<MyData>>?, t: Throwable?) {
                Log.e("**#*#*", "onFailure", t)
            }
        })
    }

    private fun takePhotoIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(activity!!, "com.fehty.instacopy.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 1)
            }
        }
    }

    private var currentPhotoPath: String? = null

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            //imageView.rotation = 90F
            //imageView.setImageURI(Uri.parse(currentPhotoPath))
            uploadDataToServer(currentPhotoPath!!)
        } else if (requestCode == 2) {
            val pickedImage = data!!.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity!!.contentResolver.query(pickedImage, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))
            //imageView.setImageURI(Uri.parse(imagePath))
            uploadDataToServer(imagePath)
            cursor.close()
        }
    }

    private fun uploadDataToServer(imagePath: String) {

        val retrofit = Retrofit.Builder()
                .baseUrl("http://138.197.150.20")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyInterface::class.java)

        val file = File(imagePath)

        val message = RequestBody.create(MediaType.parse("text/plain"), "upload")
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val fileData = MultipartBody.Part.createFormData("file", "FileName", requestFile)

        retrofit.uploadData(message, fileData).enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>?, response: Response<MyData>?) {
                Log.e("**#*#*", "onSuccess!!! ${response!!.body()}")
            }

            override fun onFailure(call: Call<MyData>?, t: Throwable?) {
                Log.e("**#*#*", "onFailure", t)
            }
        })
    }
}
