package com.fehty.instacopy.Activity.BottomNavigationFragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
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
import java.text.SimpleDateFormat
import java.util.*

class TakePhotoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermissionToTakePhoto()
    }

    fun checkStoragePermissionToTakePhoto() {
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else takePhotoIntent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_take_photo, container, false)
    }

    private var currentPhotoPath: String? = null

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

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity!!.progressBar.visibility = View.VISIBLE
        if (requestCode == 1) {
            if (currentPhotoPath != null) uploadDataToServer()
            else fragmentManager!!.beginTransaction().replace(R.id.container, HomeFragment()).commit()
        }
    }

    private fun uploadDataToServer() {

        val message = RequestBody.create(MediaType.parse("text/plain"), "")

        val file = File(currentPhotoPath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val fileData = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val tokenRealm = Realm.getDefaultInstance().where(TokenRealm::class.java).findFirst()!!.userToken
        val token = RequestBody.create(MediaType.parse("text/plain"), tokenRealm!!)

        MyApplication().retrofit.uploadMessage(message, fileData, token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                fragmentManager!!.beginTransaction().replace(R.id.container, HomeFragment()).commit()
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                fragmentManager!!.beginTransaction().replace(R.id.container, HomeFragment()).commit()
            }
        })
    }
}
