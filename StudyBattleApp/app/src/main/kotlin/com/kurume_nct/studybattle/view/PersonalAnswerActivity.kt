package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityPersonalAnswerBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.PersonalAnswerViewModel

class PersonalAnswerActivity : AppCompatActivity(), PersonalAnswerViewModel.Callback {

    private lateinit var binding: ActivityPersonalAnswerBinding
    private lateinit var unitPer: UnitPersonal
    private var writeNows = false
    private var writeNow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_answer)
        binding.personalAnswer = PersonalAnswerViewModel(this, this)
        unitPer = application as UnitPersonal
        bindSetting()
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
