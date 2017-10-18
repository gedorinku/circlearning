package com.kurume_nct.studybattle.viewModel

import android.content.ComponentCallbacks
import android.content.Context
import android.database.Observable
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/25/2017.
 */
class ItemInfoViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    lateinit var unitPer: UnitPersonal

    @Bindable
    var magicCount = "×0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.magicCount)
        }

    @Bindable
    var bombCount = "×0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.bombCount)
        }

    @Bindable
    var cardCount = "×0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardCount)
        }

    @Bindable
    var shieldCount = "×0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.shieldCount)
        }

    fun onCreate(){
        unitPer = context.applicationContext as UnitPersonal
        ServerClient(unitPer.authenticationKey)
                .getMyItems(unitPer.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    magicCount = "×" + it[0].toString()
                    bombCount = "×" +  it[1].toString()
                    cardCount = "×" + it[3].toString()
                    shieldCount = "×" + it[2].toString()
                }
    }

    interface Callback {

    }
}