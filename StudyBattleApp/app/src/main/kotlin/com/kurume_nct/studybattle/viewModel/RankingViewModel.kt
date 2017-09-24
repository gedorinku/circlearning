package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * Created by hanah on 9/25/2017.
 */
class RankingViewModel(private val context: Context, private val callback: Callback) : BaseObservable(){

    @Bindable
    var myNowScore = "334点"
    var myLastScore = "278点"

    interface Callback{

    }
}