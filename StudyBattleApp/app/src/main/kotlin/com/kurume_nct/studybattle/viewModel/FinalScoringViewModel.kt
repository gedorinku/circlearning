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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.view.FinalScoringActivity
import com.kurume_nct.studybattle.view.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/16/2017.
 */
class FinalScoringViewModel(val context: FinalScoringActivity, val callback: Callback) : BaseObservable() {

    private var url = ""
    private var problemUrl = ""
    private var writeNow = false
    private val addText = "+コメントを追加"
    private val confirmText = "+コメントを送信"
    private var correct = false
    private lateinit var solution: Solution
    private var lastCommentIndex = 0
    private var replyTo = 0

    companion object {
        @BindingAdapter("loadImageFinalScoring")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(view).load(R.drawable.no_image).into(view)//loadの中にresourceを入れたらtestできる
            } else {
                Glide.with(view).load(uri).into(view)
            }
        }
    }

    @Bindable
    var problemTitle = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.problemTitle)
    }

    @Bindable
    var personalAnswerUri: Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.personalAnswerUri)
        }

    @Bindable
    var personalProblemUri: Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.personalProblemUri)
        }


    @Bindable
    var ansCreatorName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ansCreatorName)
        }

    @Bindable
    var correctPersonal = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.correctPersonal)
        }

    @Bindable
    var imageClickAble = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageClickAble)
        }

    @Bindable
    var radioCorrect = false
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.radioCorrect)
        }

    fun onClickImageView(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", url)
        context.startActivity(intent)
    }

    fun onClickProblemView(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", problemUrl)
        context.startActivity(intent)
    }

    @Bindable
    var everyoneComment: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.everyoneComment)
        }

    @Bindable
    var yourComment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.yourComment)
        }

    @Bindable
    var commentButtonText = addText
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentButtonText)
        }

    fun onClickComment(view: View) {
        onWriteComment()
    }

    fun onClickResetButton(view: View) {
        if (correct != radioCorrect) {
            val unitPer = context.applicationContext as UsersObject
            val client = ServerClient(unitPer.authenticationKey)
            client
                    .judgeSolution(context.mSolutionId, radioCorrect)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        callback.onReset()
                    }
        }
    }

    fun getInitData() {
        val unitPer = context.applicationContext as UsersObject
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getSolution(context.mSolutionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    solution = it
                    if (it.accepted) {
                        correctPersonal = "正解"
                        correct = true
                        radioCorrect = true
                    } else {
                        correctPersonal = "間違え"
                        callback.changeTextColor()
                    }
                    client.apply {
                        getUser(it.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    ansCreatorName = it.displayName + "(" + it.userName + ")"
                                }
                        getProblem(it.problemId)
                                .flatMap {
                                    problemTitle = it.title
                                    client.getImageById(it.imageIds[0])
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    problemUrl = it.url
                                    personalProblemUri = Uri.parse(problemUrl)
                                }
                    }

                    refreshComment(false)
                    if (it.imageCount > 0)
                        client
                                .getImageById(it.imageIds[0])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    url = it.url
                                    personalAnswerUri = Uri.parse(url)
                                    imageClickAble = true
                                }
                }, {
                    it.printStackTrace()
                    failAction()
                })
    }

    private fun onWriteComment() {
        writeNow = if (writeNow) {
            if (yourComment.isNotBlank()) sendComment()
            false
        } else {
            callback.enableEditText(true)
            commentButtonText = confirmText
            true
        }
    }

    private fun sendComment() {

        replyTo = solution.authorId

        val unitPer = context.applicationContext as UsersObject
        val client = ServerClient(unitPer.authenticationKey)
        client
                .createComment(
                        solutionId = solution.id,
                        text = ("\n" + unitPer.user.displayName + "(" + unitPer.user.userName + ")" + "\n\t") + yourComment,
                        imageIds = listOf(),
                        replyTo = replyTo
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.enableEditText(false)
                    yourComment = ""
                    commentButtonText = addText
                    refreshComment(false)
                }
    }

    fun refreshComment(boolean: Boolean) {
        val unitPer = context.applicationContext as UsersObject
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getSolution(context.mSolutionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    solution = it
                    it.comments.forEachIndexed { index, comment ->
                        if (lastCommentIndex <= index)
                            everyoneComment += (comment.text + "\n")
                    }
                    lastCommentIndex = solution.comments.size
                    if (boolean) callback.finishedRefresh()
                }, {
                    it.printStackTrace()
                    Toast.makeText(context, "ネット環境の確認をお願いします。", Toast.LENGTH_SHORT).show()
                    if (boolean) callback.finishedRefresh()
                })
    }

    private fun failAction() {
        Toast.makeText(context, "解答の取得に失敗しました。", Toast.LENGTH_SHORT).show()
        Log.d("Tag", "解答の取得に失敗しました。")
        callback.onReset()
    }

    interface Callback {
        fun onReset()
        fun enableEditText(boolean: Boolean)
        fun finishedRefresh()
        fun changeTextColor()
    }
}