package com.example.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    private lateinit var cursor: Cursor
    private var handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button.setOnClickListener(this)
        back_button.setOnClickListener(this)
        start_stop_button.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImage()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            getImage()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_button -> getNextImage()
            R.id.back_button -> getBackImage()
//            R.id.start_stop_button -> start_stop_button.text = "停止"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage()
                }
        }
    }

    private fun getImage() {
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )!!

        if (cursor.moveToFirst()) {
            imageShow()
        }
    }

    private fun getNextImage() {
        if (cursor.moveToNext()) {
            imageShow()
        } else if (cursor.moveToFirst()) {
            imageShow()
        }
    }

    private fun getBackImage() {
        if (cursor.moveToPrevious()) {
            imageShow()
        } else if (cursor.moveToLast()) {
            imageShow()
        }
    }

    private fun imageShow() {
        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor.getLong(fieldIndex)
        val imageUri =  ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        Log.d("ANDROID", "URI : $imageUri")
        imageView.setImageURI(imageUri)
    }

}