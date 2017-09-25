package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.kurume_nct.studybattle.`object`.UnitPersonal

/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(private val context: Context, private val callback : Callback) : BaseObservable(){

    @Bindable
    var creatorName = ""

    @Bindable
    var problemName = ""
        get
        set(value) {
            field = value
            //notifyPropertyChanged()
        }

    @Bindable
    var termForOne = ""

    fun onClickProblemImage(view: View){

    }

    fun onClickAnswerImage(view : View){

    }

    fun onClickFinish(view: View){

    }

    interface Callback{

    }
}