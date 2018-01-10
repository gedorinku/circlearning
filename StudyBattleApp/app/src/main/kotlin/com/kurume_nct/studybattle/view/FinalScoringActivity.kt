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
    var mSolutionId = 0
    private val FINAL_SCORING_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSolutionId = intent.getIntExtra("mSolutionId", -1)
        if (mSolutionId == -1) {
            Log.d("solutionId", "inadequate")
            finish()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_scoring)

        binding.apply {
            viewModel = FinalScoringViewModel(
                    this@FinalScoringActivity,
                    this@FinalScoringActivity
            )
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
        binding.yourCommentEditText.visibility = if (boolean) View.VISIBLE else View.GONE
    }

    override fun changeTextColor() {
        binding.currentPersonalText.setTextColor(Color.BLUE)
    }
}
