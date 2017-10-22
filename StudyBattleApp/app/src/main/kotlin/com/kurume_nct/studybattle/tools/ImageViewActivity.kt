package com.kurume_nct.studybattle.tools

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityImageViewBinding
import com.kurume_nct.studybattle.databinding.ActivityItemInfoBinding

class ImageViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_view)
        val imageUrl = intent.getStringExtra("url")
        if (Uri.parse(imageUrl) != null)
            Glide.with(this).load(Uri.parse(imageUrl)).into(binding.imageView17)
    }
}
