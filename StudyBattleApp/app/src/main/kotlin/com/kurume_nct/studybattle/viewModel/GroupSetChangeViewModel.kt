package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.util.Log
import android.view.View
import com.kurume_nct.studybattle.BR

/**
 * Created by hanah on 10/1/2017.
 */
class GroupSetChangeViewModel(val context: Context, val callback: Callback): BaseObservable() {


    @Bindable
    var searchEditText = ""
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.searchEditText)
    }

    fun onClickChangeInfo(view: View){
        callback.onChange()
    }

    fun onClickGoodbyeInfo(view: View){
        callback.onGoodbye()
    }

    interface Callback{
        fun onChange()
        fun onGoodbye()
    }
}