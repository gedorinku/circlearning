package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityItemInfoBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.ItemInfoViewModel

class ItemInfoActivity : AppCompatActivity(), ItemInfoViewModel.Callback {

    lateinit var binding : ActivityItemInfoBinding
    lateinit var unitPer: UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_item_info)
        binding.itemInfo = ItemInfoViewModel(this,this)
        //持っているItemの個数をserverから取得
        unitPer = application as UnitPersonal
        binding.itemInfo.magicCount = "×" + unitPer.itemCount.magicHand.toString()
        binding.itemInfo.cardCount = "×" + unitPer.itemCount.card.toString()
        binding.itemInfo.bombCount = "×" + unitPer.itemCount.bomb.toString()
        binding.itemInfo.shieldCount = "×" + unitPer.itemCount.shield.toString()
    }
}
