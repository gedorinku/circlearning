package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ActionMenuView
import android.widget.Toast
import com.bumptech.glide.Glide

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityMadeCollectYetBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MadeCollectYetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMadeCollectYetBinding
    private var problemId = 0
    private lateinit var unitPer: UnitPersonal
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_made_collect_yet)
        problemId = intent.getIntExtra("problemId", 0)
        checkProblem(problemId)
        unitPer = application as UnitPersonal
        getProblemInfo()
        binding.problemImageView.apply {
            setOnClickListener {
                val intent = Intent(context, ImageViewActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
            }
            isClickable = false
        }
    }

    private fun checkProblem(id: Int) {
        if (id == 0) {
            Toast.makeText(this, "問題の取得に失敗", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getProblemInfo() {
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.run {
                        problemNameText.text = it.title
                        problemNowSituationText.text = stateString(it.rawState)
                        problemCollectedDateText.text = dateConverter(it.createdAt, 0) + calculatePerOneHour(it.durationMillis)
                        problemMadeDateText.text = dateConverter(it.createdAt, 0)
                        durationPerOneText.text = calculatePerOneHour(it.durationPerUserMillis)
                        problemSubmittedPeopleText.text = it.solutions.size.toString() + "人が提出済み"
                        client
                                .getImageById(it.imageIds[0])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    setUpPicture(Uri.parse(it.url))
                                    url = it.url
                                    binding.problemImageView.isClickable = true
                                }
                    }
                }
    }

    private fun stateString(state: String) =
            when (state) {
                "Opening" -> "　回収中　"
                "Judging" -> "　正誤判定中　"
                "Judged" -> "　正誤判定済み　"
                else -> {
                    "謎"
                }
            }

    private fun calculateDate(millis: Long): String {
        var dateStr = "~"
        dateStr += "月"
        dateStr += "日"
        return dateStr
    }

    private fun dateConverter(base: String, millis: Long): String {
        val hours = millis / (60 * 1000 * 60)
        var date = ""
        val ngWord = listOf('-', ':', 'T')
        var i = 0
        var j: Int
        (0..3).forEach {
            j = i
            while (!ngWord.contains(base[i])) {
                i++
            }
            //00時が気に入らなかったので
            date += if (base.subSequence(j, i).isNotBlank() && base.subSequence(j, i)[0] == '0') {
                " " + base.subSequence(j + 1, i)
            } else {
                base.subSequence(j, i)
            }
            i++
            when (it) {
                0 -> {
                    date += "年"
                    date = ""
                }
                1 -> date += "月"
                2 -> date += "日"
                3 -> date += "時"
            }
        }
        return date
    }

    private fun calculatePerOneHour(millis: Long): String {
        val hour = (millis / (60 * 60 * 1000))
        val min = ((millis - hour * 60 * 60 * 1000) / (60 * 1000))
        return hour.toString() + "時" + min.toString() + "分"
    }

    private fun setUpPicture(uri: Uri) {
        Glide.with(this).load(uri).into(binding.problemImageView)
    }

}

