package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.listFragment.AnswerFragment
import com.kurume_nct.studybattle.viewModel.AnswerViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityAnswerBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AnswerActivity : AppCompatActivity(), AnswerViewModel.Callback {

    lateinit var binding: ActivityAnswerBinding
    private var mFin: Int = 0
    lateinit var unit: UnitPersonal
    private var mProblemId = -1
    private var problemTitle = ""
    private var problemUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        unit = application as UnitPersonal
        mFin = intent.getIntExtra("fin", 0)
        mProblemId = intent.getIntExtra("problemId", -1)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer)
        binding.viewModel = AnswerViewModel(this, this)

        if (mProblemId == -1) {
            Log.d("ProblemId", "ばぐ")
            onError()
        }

        val fragment = AnswerFragment().newInstance(mFin, mProblemId)

        supportFragmentManager.beginTransaction()
                .replace(R.id.answers_fragment,fragment)
                .commit()

        if (mFin != 3) {
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
        finish()
    }

    override fun getFin() = mFin

    override fun getProblemId() = mProblemId

    override fun finishedRefresh() {
        binding.swipeRefreshAnswer.isRefreshing = false
    }
}
