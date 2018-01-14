package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.view.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/30/2017.
 */
class PersonalAnswerViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var url = ""
    private var problemUrl = ""
    private var writeNow = false
    private val addText = "+コメントを追加"
    private val comfierText = "+コメントを送信"
    private var solution = Solution()
    private var problem = Problem()
    private var lastCommentIndex = 0
    private var replyTo = 0
    private val usersObject = context.applicationContext as UsersObject
    private val client = ServerClient(usersObject.authenticationKey)

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(view).load(R.drawable.no_image).into(view)
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
    var writer = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.writer)
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
    var correctPersonal = ""
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
    var imageClickable = true
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

    fun deployData(id: Int, isProblmeId: Boolean) {
        if (isProblmeId) {
            /*need to get problemId*/
            deployProblemData(id)
        } else {
            /*need to get solutionId*/
            deploySolutionData(id)
        }
    }

    private fun deploySolutionData(solutionId: Int) {
        client
                .getSolution(solutionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    solution = it
                    if (problem == Problem()) deployProblemData(it.problemId)
                    else setupViewSolution()
                }, {
                    callback.onError()
                })
    }

    private fun deployProblemData(problemId: Int) {
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problem = it
                    if (solution == Solution()) {
                        solution = it.solutions.last { solution.id == usersObject.user.id }
                        if (solution == Solution()) throw Exception()
                    } else setupViewProblem()
                }, {
                    callback.onError()
                })
    }

    private fun setupViewProblem(){
        problem.let {
            problemTitle = it.title
            writer = "by. " + it.assumedSolution.author.displayName
        }
        client
                .getImageById(problem.imageIds[0])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problemUrl = it.url
                    personalProblemUri = Uri.parse(it.url)
                },{
                   callback.onError()
                })
    }

    private fun setupViewSolution() {
        if (!solution.judged) {
            callback.judgeYet()
        } else if (!solution.accepted) {
            correctPersonal = "間違え"
            callback.changeColor()
        } else {
            correctPersonal = "正解"
        }
        client
                .getImageById(problem.imageIds[0])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    url = it.url
                    personalAnswerUri = Uri.parse(url)
                    imageClickable = true
                }, {
                    callback.onError()
                })

        lastCommentIndex = solution.comments.size
        solution.comments.forEach { comment ->
            everyoneComment += (comment.text + "\n")
        }
    }

    private fun onWriteComment() {
        writeNow = if (writeNow) {
            if (yourComment.isNotBlank()) sendComment()
            false
        } else {
            callback.enableEditText(true)
            commentButtonText = addText
            true
        }
    }

    private fun sendComment() {
        //Hate
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
                    commentButtonText = comfierText
                    refreshComment(false)
                }
    }

    fun refreshComment(boolean: Boolean) {
        val unitPer = context.applicationContext as UsersObject
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getSolution(solution.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    solution = it
                    solution.comments.forEachIndexed { index, comment ->
                        if (index >= lastCommentIndex)
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


    interface Callback {
        fun enableEditText(boolean: Boolean)
        fun getProblemId(): Int
        fun onFinish()
        fun finishedRefresh()
        fun judgeYet()
        fun changeColor()
        fun getSwitch(): String
        fun getSolution(): Solution
        fun onError()
    }
}