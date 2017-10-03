package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.kurume_nct.studybattle.BR

/**
 * Created by hanah on 10/1/2017.
 */
class CreateGroupViewModel(val context: Context, val callback: Callback): BaseObservable() {

    @Bindable
    var groupName = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.groupName)
    }

    @Bindable
    var peopleCount = "0"
    set(value) {
        field = value
        notifyPropertyChanged(BR.peopleCount)
    }

    fun onClickMakeGroup(view: View){
        callback.makeGroup()
    }

    interface Callback{
        fun makeGroup()
    }
}