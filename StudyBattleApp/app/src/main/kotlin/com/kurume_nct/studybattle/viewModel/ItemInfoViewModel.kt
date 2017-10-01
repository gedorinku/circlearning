package com.kurume_nct.studybattle.viewModel

import android.content.ComponentCallbacks
import android.content.Context
import android.database.Observable
import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * Created by hanah on 9/25/2017.
 */
class ItemInfoViewModel(private val context: Context, private val callback : Callback) : BaseObservable(){

    @Bindable
    var magicCount = "×0"
    var bombCount = "×0"
    var cardCount = "×0"
    var shieldCount = "×0"

    interface Callback{

    }
}