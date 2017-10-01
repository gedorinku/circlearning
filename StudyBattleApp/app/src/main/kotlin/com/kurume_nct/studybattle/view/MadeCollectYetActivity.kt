package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityMadeCollectYetBinding

class MadeCollectYetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMadeCollectYetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_made_collect_yet)
        bindSetting()
    }

    private fun bindSetting(){
        binding.run {
            problemNameText.text = "問題名"
            //imageView14.setImageURI()
            proDireText.text = "~" + "10" + "月" + "1" + "日"
            proSituationPeopleText.text = "今までに" + "n" + "人が解答を提出しています"
            direForOneText.text = "n" + "時間"
            problemDateText.text = "n" + "年" + "n" + "月" + "n" + "日"
        }
    }
}
