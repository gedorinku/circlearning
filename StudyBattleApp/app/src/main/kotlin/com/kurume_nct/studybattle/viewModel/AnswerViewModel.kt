package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by hanah on 9/28/2017.
 */
class AnswerViewModel(private val context: Context, private val callback : Callback) : BaseObservable(){

    companion object {
        @BindingAdapter("ImageLoad")
        @JvmStatic
        fun imageLoad(view : ImageView, uri : Uri){
            Glide.with(view).load(uri).into(view)
        }
    }




    interface Callback{

    }
}