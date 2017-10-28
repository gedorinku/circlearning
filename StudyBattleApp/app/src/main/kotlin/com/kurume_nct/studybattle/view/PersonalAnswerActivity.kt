package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityPersonalAnswerBinding
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.PersonalAnswerViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PersonalAnswerActivity : AppCompatActivity(), PersonalAnswerViewModel.Callback {

    private lateinit var binding: ActivityPersonalAnswerBinding
    private lateinit var unitPer: UnitPersonal
    private var problemId = 0
    private var situationId = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.personalAnswer = PersonalAnswerViewModel(this, this)
        unitPer = application as UnitPersonal
        problemId = intent.getIntExtra("problemId", 0)
        situationId = intent.getBooleanExtra("fin", false)
        binding.personalAnswer.getInitData()
        binding.apply {
            commentEdit.visibility = View.GONE
        }
    }

    override fun enableEditText(boolean: Boolean) {
        binding.commentEdit.visibility.let {
            if (boolean) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun getProblemId() = problemId

    override fun onFinish() {
        finish()
    }
}
