package com.kurume_nct.studybattle.viewModel

import android.content.*
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.User
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Duration


/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(
        private val context: Context,
        private val callback: Callback
) : BaseObservable() {

    val termExtra = "(解答回収期間より)"
    private var problemImageId = 0
    private var answerImageId = 0
    private var duration: Duration? = null
    private val usersObject = (context.applicationContext as UsersObject)

    companion object {
        @BindingAdapter("loadImage")
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
    var day = "回収日が設定されていません"
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
        callback.alertDialog(true)
        Log.d("click ", "problemImage....")
        //using alert screen ans to do to choice photo or image
    }

    fun onClickAnswerImage(view: View) {
        callback.alertDialog(false)
        Log.d("click ", "answerImage....")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (requestCode) {
            1 -> {
                problemUri = data.data
                callback.onClickableButtons()
            }
            0 -> {
                answerUri = data.data
                callback.onClickableButtons()
            }
        }
    }

    fun onClickFinish(view: View) {
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

        val client = ServerClient(usersObject.authenticationKey)
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
                                                duration = duration!!,
                                                groupId = usersObject.nowGroup.id,
                                                assumedSolution = Solution(
                                                        text = "お寿司と焼き肉の戦い。",
                                                        authorId = usersObject.user.id,
                                                        imageCount = 1,
                                                        imageIds = listOf(answerImageId)
                                                )
                                        )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Log.d("問題のID", it.id.toString())
                                            dialog.dismiss()
                                            callback.createData(problemName)
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
        fun startActivityForResult(intent: Intent, requestCode: Int)
        fun createData(title: String)
        fun alertDialog(problem: Boolean)
        fun onDateDialog()
        fun onClickableButtons()
        fun onNotClickableButtons()
    }
}