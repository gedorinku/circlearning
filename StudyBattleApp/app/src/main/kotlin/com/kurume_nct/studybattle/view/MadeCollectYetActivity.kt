package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityMadeCollectYetBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MadeCollectYetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMadeCollectYetBinding
    private var problemId = 0
    private lateinit var unitPer: UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_made_collect_yet)
        problemId = intent.getIntExtra("problemId", 0)
        checkProblem(problemId)
        unitPer = application as UnitPersonal
        getProblemInfo()
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
                        problemNowSituationText.text = "問題の状態の分かるエンドポイントありますか？"
                        problemCollectedDateText.text = "収集日は計算する感じですかね・・・？"
                        problemMadeDateText.text = it.createdAt
                        durationPerOneText.text = calculatePerOneHour(it.durationMillis)
                        problemSubmittedPeopleText.text = "今までに" + "エンドポイント" + "人が解答を提出しています"
                        client
                                .getImageById(it.imageIds[0])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    setUpPicture(Uri.parse(it.url))
                                }
                    }
                }
    }

    private fun calculateDate(): String{
        var dateStr = "~"
        dateStr += "月"
        dateStr += "日"
        return dateStr
    }

    private fun calculatePerOneHour(millis: Long): String{
        val str = (millis /(60 * 60 * 1000)).toString() + "時間"
        return str
    }

    private fun setUpPicture(uri: Uri) {
        Glide.with(this).load(uri).into(binding.imageView14)
    }

}
