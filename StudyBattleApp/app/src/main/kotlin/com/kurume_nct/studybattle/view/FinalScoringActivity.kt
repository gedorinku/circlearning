package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AndroidException
import android.util.Log
import android.view.View
import android.widget.Toast

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityFinalScoringBinding
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.FinalScoringViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FinalScoringActivity : AppCompatActivity(), FinalScoringViewModel.Callback {

    private lateinit var binding: ActivityFinalScoringBinding
    private var solution = 0
    private val FINAL_SCORING_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_scoring)
        binding.viewModel = FinalScoringViewModel(this, this)
        solution = intent.getIntExtra("solutionId", -1)
        if (solution == -1) {
            Log.d("soputionId", "適切ではない")
            finish()
        }

        binding.apply {
            viewModel.getInitData()
            swipeRefreshFinal.setOnRefreshListener {
                viewModel.refreshComment(true)
            }
        }
    }

    override fun getSolutionId() = solution

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
