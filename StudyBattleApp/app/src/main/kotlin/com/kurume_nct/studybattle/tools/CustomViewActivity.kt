package com.kurume_nct.studybattle.tools

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.databinding.ActivityCustomViewBinding

class CustomViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_view)
    }
}
