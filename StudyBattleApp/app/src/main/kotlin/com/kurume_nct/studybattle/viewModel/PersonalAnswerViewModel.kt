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
import com.kurume_nct.studybattle.tools.ImageViewActivity
import com.kurume_nct.studybattle.tools.ToolClass

/**
 * Created by hanah on 9/30/2017.
 */
class PersonalAnswerViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var uri: Uri? = null

    companion object {
        @BindingAdapter("loadImagePersonalAnswer")
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
    var remainigAnsTime = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainigAnsTime)
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

    @Bindable
    var imageClickable = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.imageClickAble)
    }

    fun onClickImageView(view: View){
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url",callback.getImageUrl())
        context.startActivity(intent)
    }

    interface Callback {
        fun getImageUrl(): String
        fun onWriteComment()
        fun onWriteScores()
    }
}