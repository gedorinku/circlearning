package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.listFragment.AnswerFragment
import com.kurume_nct.studybattle.viewModel.AnswerViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityAnswerBinding
import com.kurume_nct.studybattle.model.SolutionStatus
import com.kurume_nct.studybattle.model.UsersObject

class AnswerActivity : AppCompatActivity(), AnswerViewModel.Callback {

    lateinit var binding: ActivityAnswerBinding
    lateinit var usersObject: UsersObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        usersObject = application as UsersObject
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer)

        val solutionStatus = SolutionStatus.status(intent.getIntExtra("fin", 0))
        val problemId = intent.getIntExtra("problemId", -1)

        val fragment = AnswerFragment().newInstance(solutionStatus.statementId, problemId)

        binding.viewModel = AnswerViewModel(this, this, problemId, solutionStatus)

        /*問題のidが分からなかった*/
        if (problemId == -1) {
            onError()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.answers_fragment,fragment)
                .commit()

        if (solutionStatus != SolutionStatus.ALL_FINISH) {
            binding.problemScoreAnsText.visibility = View.GONE
        }

        binding.yourCommentEditText.visibility = View.GONE

        binding.viewModel.onInitDataSet()

        binding.swipeRefreshAnswer.setOnRefreshListener {
            binding.viewModel.refreshComment(true)
            fragment.getProblemData()
        }

    }

    //ship code

    override fun visibilityEditText(boolean: Boolean) =
            if (boolean)
                binding.yourCommentEditText.visibility = View.VISIBLE
            else {
                binding.yourCommentEditText.visibility = View.GONE
            }

    override fun onError() {
        Toast.makeText(this, "問題の取得に失敗しました", Toast.LENGTH_SHORT).show()
        setResult(0)
        finish()
    }

    override fun finishedRefresh() {
        binding.swipeRefreshAnswer.isRefreshing = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(0)
    }
}
