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

/**
 * Created by hanah on 9/28/2017.
 */
class AnswerViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    //想定解を変える事を可能にしたとき用のVM

    var pUri: String = ""
    var aUri: String = ""


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
    var masterName = "(made by Nana)"
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
        context.startActivity(intent)
    }

    fun onClickAnswerImage(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", aUri)
        context.startActivity(intent)
    }


    interface Callback {

    }
}