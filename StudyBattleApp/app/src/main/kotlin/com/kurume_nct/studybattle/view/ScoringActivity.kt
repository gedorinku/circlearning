package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityScoringBinding
import com.kurume_nct.studybattle.viewModel.ScoringViewModel

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
        setResult(5)
        finish()
    }
}
