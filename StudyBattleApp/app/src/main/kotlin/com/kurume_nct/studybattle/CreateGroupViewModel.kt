package com.kurume_nct.studybattle

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.tools.ResIDToUriClass

/**
 * Created by hanah on 10/1/2017.
 */
class CreateGroupViewModel(val context: Context, val callback: Callback): BaseObservable() {

    var iUri : Uri = ResIDToUriClass().convertUrlFromDrawableResId(context,R.drawable.glad)!!

    companion object {
        @BindingAdapter("loadIcon")
        @JvmStatic
        fun setIcon(view: ImageView, uri: Uri){
            Glide.with(view).load(uri).into(view)
        }
    }

    @Bindable
    var iconUri = iUri
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.iconUri)
    }

    @Bindable
    var name = ""
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.name)
    }

    interface Callback{

    }
}