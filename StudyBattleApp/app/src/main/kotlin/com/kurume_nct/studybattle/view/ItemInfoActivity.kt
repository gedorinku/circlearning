package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityItemInfoBinding
import com.kurume_nct.studybattle.viewModel.ItemInfoViewModel

class ItemInfoActivity : AppCompatActivity(), ItemInfoViewModel.Callback {

    lateinit var binding: ActivityItemInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_info)
        binding.viewModel = ItemInfoViewModel(this)
        binding.viewModel.onCreate()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(0)
    }
}
