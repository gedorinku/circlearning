package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityFinalScoringBinding
import com.kurume_nct.studybattle.viewModel.FinalScoringViewModel

class FinalScoringActivity : AppCompatActivity(), FinalScoringViewModel.Callback {

    private lateinit var binding: ActivityFinalScoringBinding
    private val FINAL_SCORING_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_scoring)
        val solutionId = intent.getIntExtra("solutionId", -1)
        if (solutionId == -1) {
            Log.d("soputionId", "適切ではない")
            finish()
        }
        binding.viewModel = FinalScoringViewModel(this, this, solutionId)
        binding.apply {
            viewModel.getInitData()
            swipeRefreshFinal.setOnRefreshListener {
                viewModel.refreshComment(true)
            }
        }
    }

    override fun onReset() {
        setResult(FINAL_SCORING_CODE)
        finish()
    }

    override fun finishedRefresh() {
        binding.swipeRefreshFinal.isRefreshing = false
    }

    override fun enableEditText(boolean: Boolean) {
        if (boolean) {
            binding.yourCommentEditText.visibility = View.VISIBLE
        } else {
            binding.yourCommentEditText.visibility = View.GONE
        }
    }

    override fun changeTextColor() {
        binding.currentPersonalText.setTextColor(Color.BLUE)
    }
}
