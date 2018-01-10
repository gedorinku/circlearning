package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityPersonalAnswerBinding
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.viewModel.PersonalAnswerViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PersonalAnswerActivity : AppCompatActivity(), PersonalAnswerViewModel.Callback {

    private lateinit var binding: ActivityPersonalAnswerBinding
    private lateinit var usersObject: UsersObject
    var mSwitch = ""
    var mProblemId = 0
    private var situationStatus = false
    var otherSolution: Solution = Solution()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        mSwitch = intent.getStringExtra("switch")
        usersObject = application as UsersObject
        situationStatus = intent.getBooleanExtra("fin", false)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.viewModel = PersonalAnswerViewModel(this, this)

        val solutionId = intent.getIntExtra("mSolutionId", -1)

        if(mSwitch == "s"){
            ServerClient(usersObject.authenticationKey)
                    .getSolution(solutionId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        otherSolution = it
                        mProblemId = it.problemId
                        binding.viewModel.getInitData()
                    },{
                        Log.d("po","つらい")
                    })
        }else{
            mProblemId = intent.getIntExtra("mProblemId", 0)
            binding.viewModel.getInitData()
        }

        binding.apply {
            commentEdit.visibility = View.GONE
            swipeRefreshPersonal.setOnRefreshListener {
                viewModel.refreshComment(true)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(0)
    }

    override fun enableEditText(boolean: Boolean) {
        binding.commentEdit.visibility = if (boolean) View.VISIBLE else View.GONE
    }

    override fun finishedRefresh() {
        binding.swipeRefreshPersonal.isRefreshing = false
    }

    override fun judgeYet() {
        binding.currentPersonalText.visibility = View.GONE
    }

    override fun changeColor() {
        binding.currentPersonalText.setTextColor(Color.BLUE)
    }

    override fun onFinish() {
        setResult(0)
        finish()
    }
}
