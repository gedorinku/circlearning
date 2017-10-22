package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingUtil
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
    private lateinit var unitPer: UnitPersonal
    private var solutionId = 0
    private var writeScoreNow = false
    private var writeNow = false
    private lateinit var client: ServerClient
    private var soulution = Solution()
    private val FINAL_SCORING_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_scoring)
        binding.finalScoring = FinalScoringViewModel(this, this)
        solutionId = intent.getIntExtra("solutionId", -1)
        if (solutionId == -1) {
            failAction()
        }
        unitPer = application as UnitPersonal
        client = ServerClient(unitPer.authenticationKey)
        getInitData()

        binding.yourCommentEditText.visibility = View.GONE
        binding.yourScoreCommentEditText.visibility = View.GONE
    }

    private fun failAction() {
        Toast.makeText(this, "解答の取得に失敗しました。", Toast.LENGTH_SHORT).show()
        Log.d("Tag", "解答の取得に失敗しました。")
        finish()
    }

    private fun getInitData() {
        client
                .getSolution(solutionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    if (it.accepted) {
                        binding.radioScoring.check(R.id.radio_correct)
                    } else {
                        binding.radioScoring.check(R.id.radio_mistake)
                    }
                    client
                            .getUser(it.authorId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                binding.finalScoring.ansCreatorName = it.displayName + "(" + it.userName + ")"
                            }
                    //TODO getComments
                    binding.finalScoring.everyoneComment = "hoge"

                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    binding.finalScoring.personalAnswerUri = Uri.parse(it.url)
                }, {
                    failAction()
                })
    }

    override fun onWriteScores() {
        writeScoreNow = if (writeScoreNow && binding.finalScoring.yourScoreCmment.isNotBlank()) {
            binding.scoreCommentText.let {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                }
            }
            addScoreComment(binding.finalScoring.yourScoreCmment)
            binding.yourScoreCommentEditText.visibility = View.GONE
            binding.finalScoring.yourScoreCmment = ""
            false
        } else {
            binding.yourScoreCommentEditText.visibility = View.VISIBLE
            true
        }
    }

    override fun onWriteComment() {
        writeNow = if (writeNow && binding.finalScoring.yourComment.isNotBlank()) {
            binding.commentsText.let {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                }
            }
            addComment(binding.finalScoring.yourComment)
            binding.yourCommentEditText.visibility = View.GONE
            binding.finalScoring.yourComment = ""
            false
        } else {
            binding.yourCommentEditText.visibility = View.VISIBLE
            true
        }
    }

    private fun addComment(text: String) {
        binding.finalScoring.everyoneComment =
                binding.finalScoring.everyoneComment + ("\n" + text + "\n\t by " +
                        unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    private fun addScoreComment(text: String) {
        binding.finalScoring.scoreComment =
                binding.finalScoring.scoreComment + ("\n" + text + "\n\t by " +
                        unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    override fun onReset() {
        //reported server to reset
        //send comments
        //intent.putExtra("answer", (binding.radioScoring.checkedRadioButtonId == R.id.radio_correct))
        setResult(FINAL_SCORING_CODE)
        finish()
    }
}
