package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.util.Log
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UsersObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/25/2017.
 */
class RankingViewModel(
        private val context: Context,
        private val callback: Callback
) : BaseObservable() {

    @Bindable
    var myNowScore = "0点"
        set(value) {
            field = value
            notifyPropertyChanged(BR.myNowScore)
        }

    @Bindable
    var myAllScore = "0点"
        set(value) {
            field = value
            notifyPropertyChanged(BR.myAllScore)
        }

    fun onGetScore() {
        val unitPer = context.applicationContext as UsersObject
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getRanking(unitPer.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    myNowScore = it.myWeekScore.toString() + "点"
                    myAllScore = it.myTotalScore.toString() + "点"
                }, {
                    Log.d("onGetScore", "error")
                    it.printStackTrace()
                })
    }

    interface Callback {
        //none
    }
}