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
    private var idVariety = ""
    private var problemId = 0
    private var otherSolution: Solution = Solution()

    companion object {
        const val SOLUTION = "s"
        const val DATA = "switch"
        const val SOLUTIONID = "solutionId"
        const val PROBLMEID = "problemId"
        val errorId = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.viewModel = PersonalAnswerViewModel(this, this)
        usersObject = application as UsersObject
        idVariety = intent.getStringExtra(DATA)

        val id = if(idVariety == SOLUTION)
            intent.getIntExtra(SOLUTIONID, -1) else intent.getIntExtra(PROBLMEID, -1)

        if(id == errorId){
            onError()
            onFinish()
        }

        binding.viewModel.deployData(id, idVariety != SOLUTION)

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


    override fun getSolution(): Solution = otherSolution

    override fun getSwitch(): String = ""

    override fun changeColor() {
        binding.currentPersonalText.setTextColor(Color.BLUE)
    }

    override fun onFinish() {
        setResult(0)
        finish()
    }

    override fun onError(){
        Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_SHORT).show()
    }
}
