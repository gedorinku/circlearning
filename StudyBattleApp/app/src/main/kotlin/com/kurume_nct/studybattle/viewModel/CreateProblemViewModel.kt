package com.kurume_nct.studybattle.viewModel

import android.content.*
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.User
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.tools.ToolClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import java.io.File


/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    private var checkCount: Boolean = false
    private var termOne: Double
    val termExtra = "(解答回収期間より)"
    var problemImageId = 0
    var answerImageId = 0

    init {
        termOne = 24.0
       /* pUri = null*//*ToolClass(context).convertUrlFromDrawableResId(R.drawable.plus)*//*
        aUri = null*//*ToolClass(context).convertUrlFromDrawableResId(R.drawable.plus)*/
    }

    companion object {
        @BindingAdapter("loadProblem")
        @JvmStatic
        fun setCreateImage(view: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(view).load(R.drawable.plus).into(view)
            }else {
                Glide.with(view).load(uri).into(view)
            }
        }
    }


    @Bindable
    var creatorName = ""

    @Bindable
    var problemName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var termForOne = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.termForOne)
        }

    @Bindable
    var problemUri: Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    @Bindable
    var day = "5日"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.day)
        }

    fun onClickDateChange(view: View) {
        callback.onDateDialog()
    }

    @Bindable
    var answerUri: Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUri)
        }

    fun onClickProblemImage(view: View) {
        callback.alertDialog(1)
        Log.d("click ", "problemImage....")
        //using alert screen ans to do to choice photo or image
    }

    fun onClickAnswerImage(view: View) {
        callback.alertDialog(2)
        Log.d("click ", "answerImage....")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (requestCode) {
            1 -> {
                problemUri = data.data
                callback.onClickableButtons()
            }
            2 -> {
                answerUri = data.data
                callback.onClickableButtons()
            }
        }
    }

    fun onClickFinish(view: View) {
        val a = ToolClass(context).convertUrlFromDrawableResId(R.drawable.group)
        if (problemUri == null || answerUri == null || problemName.isEmpty() || problemName.isBlank() || termForOne.isBlank()) {
            Toast.makeText(context, "入力に不備があります(`・ω・´)", Toast.LENGTH_SHORT).show()
        } else {
            sendData()
        }
    }

    private fun sendData() {
        val dialogClass = ProgressDialogTool(context)
        val dialog = dialogClass.makeDialog()
        dialog.show()
        callback.onNotClickableButtons()

        val client = ServerClient(callback.getKey())
        client
                .uploadImage(problemUri!!, context)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    problemImageId = it.id
                    client
                            .uploadImage(answerUri!!, context)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                answerImageId = it.id
                                client
                                        .createProblem(
                                                title = problemName,
                                                text = "ごめんなさい",
                                                imageIds = listOf(problemImageId),
                                                startsAt = dateTime(),
                                                duration = callback.getDuration(),
                                                groupId = callback.getGroupId(),
                                                assumedSolution = Solution(
                                                        text = "お寿司と焼き肉の戦い。",
                                                        authorId = callback.userInformation().id,
                                                        imageCount = 1,
                                                        imageIds = listOf(answerImageId)
                                                )
                                        )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Log.d("問題のID", it.id.toString())
                                            dialog.dismiss()
                                            callback.getCreateData(problemName)
                                        }, {
                                            dialog.dismiss()
                                            Toast.makeText(context, "問題作成に失敗しなした。もう一度送りなおしてください。", Toast.LENGTH_SHORT).show()
                                            callback.onClickableButtons()
                                            it.printStackTrace()
                                        })
                            }, {
                                dialog.dismiss()
                                callback.onClickableButtons()
                                it.printStackTrace()
                            })
                }, {
                    dialog.dismiss()
                    callback.onClickableButtons()
                    it.printStackTrace()
                })
    }

    private fun dateTime(): DateTime = DateTime.now()

    interface Callback {

        fun userInformation(): User

        fun getKey(): String

        fun startActivityForResult(intent: Intent, requestCode: Int)

        fun getCreateData(title: String)

        fun alertDialog(pro: Int)

        fun onDateDialog()

        fun getDuration(): Duration

        fun getGroupId(): Int

        fun onClickableButtons()

        fun onNotClickableButtons()
    }
}