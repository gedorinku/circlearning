package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 10/16/2017.
 */
class FinalScoringViewModel(val context: Context, val callback: Callback): BaseObservable() {

    private var uri: Uri? = null

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
    var personalAnswerUri = uri
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
        callback.onWriteScores()
    }


    @Bindable
    var everyoneComment : String = ""
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

    fun onClickComment(view: View) {
        callback.onWriteComment()
    }

    fun onClickResetButton(view: View){
        callback.onReset()
    }

    interface Callback {

        fun onWriteScores()

        fun onWriteComment()

        fun onReset()
    }
}