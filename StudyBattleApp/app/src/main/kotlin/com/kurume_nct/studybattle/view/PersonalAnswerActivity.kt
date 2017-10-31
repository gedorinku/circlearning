package com.kurume_nct.studybattle.view

import android.content.Context
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.graphics.Color
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
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.PersonalAnswerViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PersonalAnswerActivity : AppCompatActivity(), PersonalAnswerViewModel.Callback {

    private lateinit var binding: ActivityPersonalAnswerBinding
    private lateinit var unitPer: UnitPersonal
    private var switch = ""
    private var problemId = 0
    private var situationId = false
    private var otherSolution: Solution = Solution()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.personalAnswer = PersonalAnswerViewModel(this, this)
        unitPer = application as UnitPersonal
        switch = intent.getStringExtra("switch")
        situationId = intent.getBooleanExtra("fin", false)
        if(switch == "s"){
            ServerClient(unitPer.authenticationKey)
                    .getSolution(intent.getIntExtra("solutionId", -1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        otherSolution = it
                        problemId = it.problemId
                        binding.personalAnswer.getInitData()
                    },{
                        Log.d("po","つらい")
                    })
        }else{
            problemId = intent.getIntExtra("problemId", 0)
            binding.personalAnswer.getInitData()
        }
        binding.apply {
            commentEdit.visibility = View.GONE
            swipeRefreshPersonal.setOnRefreshListener {
                personalAnswer.refreshComment(true)
            }
        }
    }

    override fun enableEditText(boolean: Boolean) {
        if (boolean)
            binding.commentEdit.visibility = View.VISIBLE
        else {
            binding.commentEdit.visibility = View.GONE
        }
    }

    override fun finishedRefresh() {
        binding.swipeRefreshPersonal.isRefreshing = false
    }

    override fun getProblemId() = problemId

    override fun judgeYet() {
        binding.currentPersonalText.visibility = View.GONE
    }

    override fun getSolution(): Solution = otherSolution

    override fun getSwitch(): String = switch

    override fun changeColor() {
        binding.currentPersonalText.setTextColor(Color.BLUE)
    }

    override fun onFinish() {
        finish()
    }
}
