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
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/16/2017.
 */
class FinalScoringViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var url = ""
    private var problemUrl = ""
    private var problemTitle = ""
    private var writeNow = false
    private var writeScoreNow = false
    private val addText = "+コメントを追加"
    private val comfilmText = "+コメントを送信"
    private var correct = false

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
    var correctPersonal = "正解"


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


    fun onClickScoreComment(view: View) {
        onWriteScores()
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
    var commentEditText = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentEditText)
        }

    @Bindable
    var scoreCommentEditText = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.scoreCommentEditText)
        }

    @Bindable
    var commentButtonText = addText
    set(value) {
        field = value
        notifyPropertyChanged(BR.commentButtonText)
    }

    @Bindable
    var scoreCommentButtonText = comfilmText
    set(value) {
        field = value
        notifyPropertyChanged(BR.scoreCommentButtonText)
    }

    fun onClickComment(view: View) {
        onWriteComment()
    }

    fun onClickResetButton(view: View) {
        if(correct != radioCorrect){
            val unitPer = context.applicationContext as UnitPersonal
            val client = ServerClient(unitPer.authenticationKey)
            client
                    .judgeSolution(callback.getSolutionId(), radioCorrect)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        callback.onReset()
                    }
        }
    }

    fun getInitData() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getSolution(callback.getSolutionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    if (it.accepted) {
                        correct = true
                        radioCorrect = true
                    }/* else {
                        radioMiss = true
                    }*/
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
                    imageClickAble = true
                }, {
                    failAction()
                })
    }


    private fun onWriteScores() {
        writeScoreNow = if (writeScoreNow && yourScoreCmment.isNotBlank()) {
            scoreCommentEditText.let {
                if (it == View.GONE) View.VISIBLE
            }
            addScoreComment(yourScoreCmment)
            scoreCommentEditText = View.GONE
            yourScoreCmment = ""
            scoreCommentButtonText = addText
            false
        } else {
            scoreCommentEditText = View.VISIBLE
            scoreCommentButtonText = comfilmText
            true
        }
    }


    private fun onWriteComment() {
        writeNow = if (writeNow && yourComment.isNotBlank()) {
            commentEditText.let {
                if (it == View.GONE) View.VISIBLE
            }
            addComment(yourComment)
            commentEditText = View.GONE
            yourComment = ""
            commentButtonText = addText
            false
        } else {
            commentEditText = View.VISIBLE
            commentButtonText = comfilmText
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


    private fun failAction() {
        Toast.makeText(context, "解答の取得に失敗しました。", Toast.LENGTH_SHORT).show()
        Log.d("Tag", "解答の取得に失敗しました。")
        callback.onReset()
    }

    interface Callback {

        fun getSolutionId(): Int

        fun onReset()

    }
}