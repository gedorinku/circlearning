package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityPersonalAnswerBinding
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ToolClass
import com.kurume_nct.studybattle.viewModel.PersonalAnswerViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PersonalAnswerActivity : AppCompatActivity(), PersonalAnswerViewModel.Callback {

    private lateinit var binding: ActivityPersonalAnswerBinding
    private lateinit var unitPer: UnitPersonal
    private var writeNows = false
    private var writeNow = false
    private var problemId = 0
    private var problem: Problem = Problem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.personalAnswer = PersonalAnswerViewModel(this, this)
        unitPer = application as UnitPersonal
        problemId = intent.getIntExtra("id", 0)
        //getProblemInformation()
        binding.personalAnswer.personalAnswerUri = ToolClass().convertUrlFromDrawableResId(this, R.drawable.no_image)
        bindSetting()
    }

    fun getProblemInformation() {
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problem = it
                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                binding.personalAnswer.personalAnswerUri = Uri.parse(it.url)
                            }, {
                                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                            })
                }, {
                    Toast.makeText(this, "問題の情報を得られませんでした", Toast.LENGTH_SHORT).show()
                    finish()
                })
    }

    private fun bindSetting() {

        if (intent.getBooleanExtra("fin", false)) run {
            binding.scoreCommentLayoutText.visibility = View.GONE
        } else {
            binding.currentPersonalText.visibility = View.GONE
        }
        if (binding.personalAnswer.everyoneComment.isBlank()) run {
            binding.commentsText.visibility = View.GONE
        }
        if (binding.personalAnswer.scoreComment.isBlank()) run {
            binding.scoreCommentText.visibility = View.GONE
        }

        binding.yourCommentEditText.visibility = View.GONE
        binding.yourScoreCommentEditText.visibility = View.GONE
    }

    private fun addComment(text: String) {
        binding.personalAnswer.everyoneComment =
                binding.personalAnswer.everyoneComment + ("\n" + text + "\n\t by " +
                        unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    private fun addScoreComment(text: String) {
        binding.personalAnswer.scoreComment =
                binding.personalAnswer.scoreComment + ("\n" + text + "\n\t by " +
                        unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    override fun onWriteComment() {
        writeNow = if (writeNow && binding.personalAnswer.yourComment.isNotBlank()) {
            binding.commentsText.let {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                }
            }
            addComment(binding.personalAnswer.yourComment)
            binding.yourCommentEditText.visibility = View.GONE
            binding.personalAnswer.yourComment = ""
            false
        } else {
            binding.yourCommentEditText.visibility = View.VISIBLE
            true
        }
    }

    override fun onWriteScores() {
        writeNows = if (writeNows && binding.personalAnswer.yourScoreCmment.isNotBlank()) {
            binding.scoreCommentText.let {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                }
            }
            addScoreComment(binding.personalAnswer.yourScoreCmment)
            binding.yourScoreCommentEditText.visibility = View.GONE
            binding.personalAnswer.yourScoreCmment = ""
            false
        } else {
            binding.yourScoreCommentEditText.visibility = View.VISIBLE
            true
        }
    }

}
