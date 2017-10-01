package com.kurume_nct.studybattle.view

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatCallback
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityScoringBinding
import com.kurume_nct.studybattle.model.UnitPersonal

class ScoringActivity : AppCompatActivity() {

    //bindすることが少なかったのでMV
    lateinit var binding: ActivityScoringBinding
    private var scoreBoolean = true
    lateinit var unit : UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoring)
        unit = UnitPersonal()
        scoreSetting()
        bindSetting()
    }

    fun scoreSetting() {
        binding.radioScoring.checkedRadioButtonId.compareTo(R.id.radio_correct)
        binding.radioScoring.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_correct -> {
                    scoreBoolean = false
                }
                R.id.radio_mistake -> {
                    scoreBoolean = true
                }
            }
        }
    }

    private fun bindSetting() {
        binding.creatorNameAtScore.text = "Answer by " + unit.userName
        //binding.problemImageAtScore.setImageURI()
        binding.finButton.setOnClickListener {
            //send score
            intent.putExtra("Result", scoreBoolean)
            setResult(5, intent)
            finish()
        }
    }

}
