package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.bumptech.glide.Glide

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityMadeCollectYetBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.util.*

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
                        proDireText.text = calculateDate()
                        proSituationPeopleText.text = "今までに" + "エンドポイント" + "人が解答を提出しています"
                        direForOneText.text = calculateOwnMin(it.durationMillis)
                        problemDateText.text = it.rawStartsAt
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

    private fun calculateOwnMin(min: Long): String{
        var str = (min/60).toString() + "時間"
        return str
    }

    private fun setUpPicture(uri: Uri) {
        Glide.with(this).load(uri).into(binding.imageView14)
    }

    private fun bindSetting() {
        binding.run {
            problemNameText.text = "問題名"
            //imageView14.setImageURI()
            proDireText.text = "~" + "10" + "月" + "1" + "日"
            proSituationPeopleText.text = "今までに" + "n" + "人が解答を提出しています"
            direForOneText.text = "n" + "時間"
            problemDateText.text = "n" + "年" + "n" + "月" + "n" + "日"
        }
    }
}
