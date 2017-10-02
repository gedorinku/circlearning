package com.kurume_nct.studybattle.bug

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.kurume_nct.studybattle.BR

/**
 * Created by hanah on 10/1/2017.
 */
class SelectMainPeopleViewModel(val context: Context): BaseObservable(){

    //closed now
    @Bindable
    var searchText = ""
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.searchText)
    }

}