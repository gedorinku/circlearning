package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kurume_nct.studybattle.listFragment.AnswerFragment
import com.kurume_nct.studybattle.viewModel.AnswerViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityAnswerBinding
import com.kurume_nct.studybattle.model.UnitPersonal

class AnswerActivity : AppCompatActivity(), AnswerViewModel.Callback {

    lateinit var binding: ActivityAnswerBinding
    private var fin: Int = 0
    lateinit var unit: UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        unit = application as UnitPersonal
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer)
        binding.answerAct = AnswerViewModel(this, this)
        fin = intent.getIntExtra("fin", 0)
        supportFragmentManager.beginTransaction()
                .replace(R.id.answers_fragment, AnswerFragment().newInstance(fin))
                .commit()
        if (fin != 2) run {
            binding.problemScoreAnsText.visibility = View.GONE
        }
    }

    fun bindSetting() {
        //binding.problemImageAtAnswer.setImageURI()
        //binding.answerImageAtAnswer.setImageURI()
    }
}
