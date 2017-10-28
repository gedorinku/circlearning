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
    private var fin: Int = 0
    lateinit var unit: UnitPersonal
    private var problemId = -1
    private var problemTitle = ""
    private var problemUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        unit = application as UnitPersonal
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer)
        binding.answerAct = AnswerViewModel(this, this)
        fin = intent.getIntExtra("fin", 0)
        problemId = intent.getIntExtra("problemId", -1)

        if (problemId == -1) {
            Log.d("ProblemId", "ばぐ")
            failAction()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.answers_fragment,
                        AnswerFragment().newInstance(fin, problemId, problemTitle, problemUrl))
                .commit()
        if (fin != 3) {
            binding.problemScoreAnsText.visibility = View.GONE
        }

        onInitDataSet()
    }

    private fun onInitDataSet() {
        val client = ServerClient(unit.authenticationKey)
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problemTitle = it.title
                    binding.answerAct.problemName = problemTitle
                    client
                            .getUser(it.ownerId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                binding.answerAct.masterName = it.displayName + "(" + it.userName + ")"
                            }
                    if (fin == 3) {
                        //score
                    }
                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                problemUrl = it.url
                                binding.answerAct.problemUri = Uri.parse(problemUrl)
                            }
                    if (it.solutions.isNotEmpty())
                        client
                                .getImageById(it.solutions[0].imageIds[0])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    binding.answerAct.answerUri = Uri.parse(it.url)
                                })
                }, {
                    Log.d("Rxbug", "ばぐ")
                    it.printStackTrace()
                    //failAction()
                })
    }

    private fun failAction() {
        Toast.makeText(this, "問題の取得に失敗しました", Toast.LENGTH_SHORT).show()
        finish()
    }
}
