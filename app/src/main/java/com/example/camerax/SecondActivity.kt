package com.example.camerax

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.net.URI

class SecondActivity : AppCompatActivity() {
    lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        imageView = findViewById(R.id.imageView)
        val link = intent.getStringExtra("imageURL").toString()
        imageView.setImageURI(Uri.parse(link))
    }
}