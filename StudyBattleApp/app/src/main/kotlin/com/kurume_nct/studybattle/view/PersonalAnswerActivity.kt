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
    private var problemId = 0
    private var situationId = false
    private var solution: Solution? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        val switch = intent.getStringExtra("switch") == "s"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        usersObject = application as UsersObject
        situationId = intent.getBooleanExtra("fin", false)
        //todo
        if(switch){
            ServerClient(usersObject.authenticationKey)
                    .getSolution(intent.getIntExtra("solutionId", -1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        solution = it
                        problemId = it.problemId
                    },{
                        Log.d("po","つらい")
                    })
        }else{
            problemId = intent.getIntExtra("problemId", 0)
        }

        binding.viewModel = PersonalAnswerViewModel(this, this, problemId = problemId, solution = solution)
        binding.viewModel.getInitData()

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
        if (boolean)
            binding.commentEdit.visibility = View.VISIBLE
        else {
            binding.commentEdit.visibility = View.GONE
        }
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
