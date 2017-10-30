package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/28/2017.
 */
class AnswerViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    //想定解を変える事を可能にしたとき用のVM

    var pUri: String = ""
    var aUri: String = ""
    private var writeScoreNow = false
    private val addText = "+コメントを追加"
    private val confirmText = "+コメントを送信"
    private var solutionId = 0
    private var replyTo = 0
    private var lastCommentIndex = 0


    companion object {
        @BindingAdapter("AnswerImageLoad")
        @JvmStatic
        fun imageLoad(view: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(view).load(R.drawable.group).into(view)
            } else {
                Glide.with(view).load(uri).into(view)
            }
        }
    }

    @Bindable
    var problemName = "problem name🐰"
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var masterName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.masterName)
        }

    @Bindable
    var problemScore = "0点"
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemScore)
        }

    @Bindable
    var problemUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    @Bindable
    var answerUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUri)
        }

    fun onClickProblemImage(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", pUri)
        if (masterName.isNotBlank()) context.startActivity(intent)
    }

    fun onClickAnswerImage(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", aUri)
        if (masterName.isNotBlank()) context.startActivity(intent)
    }

    @Bindable
    var comment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }

    @Bindable
    var yourComment = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.yourComment)
        }

    @Bindable
    var scoreCommentButtonText = addText
        set(value) {
            field = value
            notifyPropertyChanged(BR.scoreCommentButtonText)
        }

    fun onClickCommentButton(view: View) {
        onWriteComment()
    }

    private fun onWriteComment() {
        Log.d(writeScoreNow.toString() + " ", yourComment)
        writeScoreNow = if (writeScoreNow) {
            if (yourComment.isNotBlank()) sendComment()
            false
        } else {
            callback.visibilityEditText(true)
            scoreCommentButtonText = confirmText
            true
        }
    }

    private fun sendComment() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .createComment(
                        solutionId = solutionId,
                        text = yourComment,
                        imageIds = listOf(),
                        replyTo = replyTo
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    comment = ""
                    callback.visibilityEditText(false)
                    yourComment = ""
                    scoreCommentButtonText = addText
                    refreshComment(false)
                }
    }

    fun refreshComment(boolean: Boolean){
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(callback.getProblemId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.assumedSolution.comments.forEachIndexed { index, comment1 ->
                        if (index >= lastCommentIndex)
                            client
                                    .getUser(comment1.authorId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        comment += (it.displayName + "(" + it.userName + ")" + "\n")
                                        comment += (comment1.text + "\n")
                                    }
                    }
                    lastCommentIndex = it.assumedSolution.comments.size
                    if(boolean)callback.finishedRefresh()
                }
    }

    fun onInitDataSet() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(callback.getProblemId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problem ->
                    replyTo = problem.ownerId
                    solutionId = problem.assumedSolution.id
                    problemName = problem.title
                    problemScore = " " + problem.point.toString() + "点"
                    lastCommentIndex = problem.assumedSolution.comments.size
                    problem.assumedSolution.comments.forEach { it ->
                        client
                                .getUser(it.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    comment += (it.displayName + "(" + it.userName + ")" + "\n")
                                    comment += ("\t" + problem.text + "\n")
                                }
                    }
                    client
                            .getUser(problem.ownerId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                masterName = " 作成者:" + it.displayName
                            }
                    client
                            .getImageById(problem.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                pUri = it.url
                                problemUri = Uri.parse(it.url)
                            }
                    client
                            .getImageById(problem.assumedSolution.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                aUri = it.url
                                answerUri = Uri.parse(it.url)
                            })
                }, {
                    Log.d("Rxbug", "ばぐ")
                    it.printStackTrace()
                    callback.onError()
                })
    }

    interface Callback {
        fun visibilityEditText(boolean: Boolean)
        fun onError()
        fun getFin(): Int
        fun getProblemId(): Int
        fun finishedRefresh()
    }
}