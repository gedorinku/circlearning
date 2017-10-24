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
import com.kurume_nct.studybattle.databinding.ActivityPersonalAnswerBinding
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
    private var writeNow = false
    private var writeScoreNow = false
    private val addText = "+コメントを追加"
    private val comfierText = "+コメントを送信"

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
    var scoreComment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.scoreComment)
        }

    @Bindable
    var yourScoreCmment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.yourScoreCmment)
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

    fun onClickImageView(view: View){
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

    var commentEditText: Boolean = false

    var scoreCommentEditText: Boolean = false

    @Bindable
    var commentButtonText = addText
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentButtonText)
        }

    @Bindable
    var scoreCommentButtonText = comfierText
        set(value) {
            field = value
            notifyPropertyChanged(BR.scoreCommentButtonText)
        }

    fun onClickComment(view: View) {
        onWriteComment()
    }

    fun onClickScoreComment(view: View) {
        onWriteComment()
    }

    fun getInitData() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        //todo solutionを受け取りたい！
        val solutionId = 0
        client
                .getSolution(solutionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
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
                    //TODO getComments
                    everyoneComment = "hoge"

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


    private fun onWriteScores() {
        writeScoreNow = if (writeScoreNow && yourScoreCmment.isNotBlank()) {
            callback.visibilityEditText(true, false)
            addScoreComment(yourScoreCmment)
            //TODO sent
            yourScoreCmment = ""
            scoreCommentButtonText = addText
            false
        } else {
            callback.visibilityEditText(true, true)
            scoreCommentButtonText = comfierText
            true
        }
    }


    private fun onWriteComment() {
        writeNow = if (writeNow && yourComment.isNotBlank()) {
            addComment(yourComment)
            callback.visibilityEditText(false, false)
            //TODO sent
            yourComment = ""
            commentButtonText = comfierText
            false
        } else {
            callback.visibilityEditText(false, true)
            commentButtonText = addText
            true
        }
    }

    private fun addComment(text: String) {
        val unitPer = context.applicationContext as UnitPersonal
        everyoneComment += ("\n" + text + "\n\t by " +
                unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    private fun addScoreComment(text: String) {
        val unitPer = context.applicationContext as UnitPersonal
        scoreComment += ("\n" + text + "\n\t by " +
                unitPer.myInfomation.displayName + "(" + unitPer.myInfomation.userName + ")" + "\n")
    }

    interface Callback {

        fun visibilityEditText(score: Boolean, boolean: Boolean)

        fun getProblemId(): Int

        fun onFinish()

    }
}