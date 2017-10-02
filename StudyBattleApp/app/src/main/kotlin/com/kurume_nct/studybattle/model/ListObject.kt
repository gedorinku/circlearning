package com.kurume_nct.studybattle.model

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.tools.ResIDToUriClass

/**
 * Created by hanah on 9/22/2017.
 */
data class Person_(var name : String = "gedohusa", var score : String = "334点", var id : Int = 0, var icon_id : Int = 0)

data class EveryAns(var id: Int = 0, var collect : Boolean = false, var name: String = "の解答", var fin : Boolean = false)

data class Direction(var num : String = "6")

//data class People(var uri: Uri, var name: String = "hunachi", var id: Int = 0, var selected: Boolean = false)

class JoinPeople: BaseObservable(){

    var id: Int = 0
    var selected: Boolean = false

    @Bindable
    var iconUri : Uri? = null
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.iconUri)
        }

    @Bindable
    var name = "すし"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }
}