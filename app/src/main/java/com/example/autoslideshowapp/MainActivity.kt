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
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private lateinit var cursor: Cursor
    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button.setOnClickListener(this)
        back_button.setOnClickListener(this)
        start_stop_button.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setFirstImage()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            setFirstImage()
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
                    setFirstImage()
                } else {
                    Toast.makeText(applicationContext, "permission denied", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_button
            -> if (mTimer == null) {
                setNextImage()
            }
            R.id.back_button
            -> if (mTimer == null) {
                setBackImage()
            }
            R.id.start_stop_button
            -> if (mTimer == null) {
                start_stop_button.text = "停止"
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            setNextImage()
                        }
                    }
                }, 2000, 2000)
            } else if (mTimer != null) {
                start_stop_button.text = "再生"
                mTimer!!.cancel()
                mTimer = null
            }
        }
    }

    private fun setFirstImage() {
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )!!

        if (cursor.moveToFirst()) {
            setImage()
        }
    }

    private fun setNextImage() {
        if (cursor.moveToNext()) {
            setImage()
        } else if (cursor.moveToFirst()) {
            setImage()
        }
    }

    private fun setBackImage() {
        if (cursor.moveToPrevious()) {
            setImage()
        } else if (cursor.moveToLast()) {
            setImage()
        }
    }

    private fun setImage() {
        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        Log.d("ANDROID", "URI : $imageUri")
        imageView.setImageURI(imageUri)
    }

}