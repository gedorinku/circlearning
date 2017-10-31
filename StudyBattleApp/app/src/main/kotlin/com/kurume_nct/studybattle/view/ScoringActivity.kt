package com.kurume_nct.studybattle.view

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatCallback
import android.util.AndroidException
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityScoringBinding
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.FinalScoringViewModel
import com.kurume_nct.studybattle.viewModel.ScoringViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ScoringActivity : AppCompatActivity(), ScoringViewModel.Callback {

    lateinit var binding: ActivityScoringBinding
    private var solutionId = 0
    private var problemUrl = ""
    private var problemTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoring)
        solutionId = intent.getIntExtra("solutionId", 0)
        binding.viewModel = ScoringViewModel(this, this)
        binding.viewModel.onCreate()
    }

    override fun getProblem(): Pair<String, String> = Pair(problemTitle, problemUrl)

    override fun getSolution() = solutionId

    override fun onFinish() {
        //setResult(5, intent)
        finish()
    }
}
