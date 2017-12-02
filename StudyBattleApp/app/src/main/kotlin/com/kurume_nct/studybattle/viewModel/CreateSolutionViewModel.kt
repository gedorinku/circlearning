package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R

/**
 * 🍣 Created by hanah on 2017/12/02.
 */
class CreateSolutionViewModel(context: Context) : BaseObservable() {

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setImage(imageView: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(imageView).load(R.drawable.plus).into(imageView)
            } else {
                Glide.with(imageView).load(uri).into(imageView)
            }
        }
    }

    @Bindable
    var problemName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var timeRemaining = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.timeRemaining)
        }

    @Bindable
    var writer = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.writer)
        }

    @Bindable
    var problemUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    fun onClickProblemImage(view: View) {

    }

    @Bindable
    var answerUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUri)
        }

    fun onClickAnswerImage(view: View) {

    }

    @Bindable
    var itemImageUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.itemImageUri)
        }

    fun onClickItemImage(view: View) {

    }

    fun onClickSubmit(view: View) {

    }

    fun onClickPass(view: View) {

    }

    interface Callback {

    }
}