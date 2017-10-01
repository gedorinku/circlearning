package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 9/28/2017.
 */
class AnswerViewModel(private val context: Context, private val callback : Callback) : BaseObservable(){

    //想定解を変える事を可能にしたとき用のVM

    private var pUri : Uri? = null
    private var aUri : Uri? = null


    companion object {
        @BindingAdapter("AnswerImageLoad")
        @JvmStatic
        fun imageLoad(view : ImageView, uri : Uri?){
            if (uri == null){
                Glide.with(view).load(R.drawable.group).into(view)
            }else {
                Glide.with(view).load(uri).into(view)
            }
        }
    }

    @Bindable
    var problemNameAns = "problem name🐰"

    @Bindable
    var masterNameAns = "(made by Nana)"

    @Bindable
    var problemScoreAns = "0点"

    //@Bindable
    var problemUriAns = pUri

    //@Bindable
    var answerUriAns = aUri

    interface Callback{

    }
}