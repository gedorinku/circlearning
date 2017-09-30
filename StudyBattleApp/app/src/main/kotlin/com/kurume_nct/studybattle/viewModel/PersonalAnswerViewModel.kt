package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.kurume_nct.studybattle.BR

/**
 * Created by hanah on 9/30/2017.
 */
class PersonalAnswerViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    @Bindable
    var ansCreatorName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ansCreatorName)
        }

    @Bindable
    var correctPersonal = "正解"

    @Bindable
    var remainigAnsTime = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainigAnsTime)
        }

    @Bindable
    var scoreComment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.scoreComment)
        }

    @Bindable
    var yourScoreCmment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.yourScoreCmment)
        }


    fun onClickScoreComment(view: View) {
        callback.onWriteScores()
    }

    @Bindable
    var everyoneComment : String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.everyoneComment)
        }

    @Bindable
    var yourComment = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.yourComment)
        }

    fun onClickComment(view: View) {
        callback.onWriteComment()
    }

    interface Callback {
        fun onWriteComment()
        fun onWriteScores()
    }
}