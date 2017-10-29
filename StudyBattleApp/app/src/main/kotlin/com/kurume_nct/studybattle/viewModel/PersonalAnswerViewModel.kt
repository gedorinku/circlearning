package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/30/2017.
 */
class PersonalAnswerViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var url = ""
    private var problemUrl = ""
    private lateinit var problem: Problem
    private var writeNow = false
    private val addText = "+コメントを追加"
    private val comfierText = "+コメントを送信"
    private lateinit var solution: Solution
    private var lastCommentIndex = 0
    private var replyTo = 0

    companion object {
        @BindingAdapter("loadImagePersonal")
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
    var personalProblemUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.personalProblemUri)
        }

    @Bindable
    var personalAnswerUri: Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.personalAnswerUri)
        }

    @Bindable
    var ansCreatorName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ansCreatorName)
        }

    @Bindable
    var correctPersonal = "正解"
        set(value) {
            field = value
            notifyPropertyChanged(BR.correctPersonal)
        }

    @Bindable
    var remainigAnsTime = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainigAnsTime)
        }

    @Bindable
    var imageClickable = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageClickAble)
        }

    fun onClickProblemView(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", problemUrl)
        context.startActivity(intent)
    }

    fun onClickImageView(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", url)
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

    fun getInitData() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(callback.getProblemId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    //find owner solution.
                    it.solutions.forEach {
                        if (it.authorId == unitPer.myInfomation.id) {
                            solution = it
                        }
                    }
                    //solutionが見つからないと爆発する。
                    client.apply {
                        getUser(solution.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    ansCreatorName = it.displayName + "(" + it.userName + ")"
                                }
                        getProblem(solution.problemId)
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
                    lastCommentIndex = solution.comments.size
                    solution.comments.forEach { comment ->
                        client
                                .getUser(comment.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    everyoneComment += (it.displayName + "(" + it.userName + ")" + "\n")
                                }
                        everyoneComment += (comment.text + "\n")
                    }
                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    url = it.url
                    personalAnswerUri = Uri.parse(url)
                    imageClickable = true
                }, {
                    callback.onFinish()
                })
    }


    private fun onWriteComment() {
        writeNow = if (writeNow) {
            if (yourComment.isNotBlank()) sendComment()
            false
        } else {
            callback.enableEditText(true)
            commentButtonText = addText
            writeNow = true
            true
        }
    }

    private fun sendComment() {
        //Hate
        replyTo = solution.authorId

        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .createComment(
                        solutionId = solution.id,
                        text = yourComment,
                        imageIds = listOf(),
                        replyTo = replyTo
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.enableEditText(false)
                    yourComment = ""
                    commentButtonText = comfierText
                    solution.comments.forEachIndexed { index, comment ->
                        if(index >= lastCommentIndex)
                        client
                                .getUser(comment.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    everyoneComment += (it.displayName + "(" + it.userName + ")" + "\n")
                                    everyoneComment += (comment.text + "\n")
                                }
                    }
                    lastCommentIndex = solution.comments.size
                }
    }

    interface Callback {

        fun enableEditText(boolean: Boolean)

        fun getProblemId(): Int

        fun onFinish()

    }
}