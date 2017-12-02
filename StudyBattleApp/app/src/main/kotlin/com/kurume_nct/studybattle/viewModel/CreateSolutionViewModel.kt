package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R

/**
 * 🍣 Created by hanah on 2017/12/02.
 */
class CreateSolutionViewModel(context: Context): BaseObservable(){

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setImage(imageView: ImageView, uri: Uri?){
            if(uri == null){
                Glide.with(imageView).load(R.drawable.plus).into(imageView)
            }else{
                Glide.with(imageView).load(uri).into(imageView)
            }
        }
    }

    interface Callback{

    }
}