package com.kurume_nct.studybattle.viewModel

import android.content.Context
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
    var magicNumber = 0
    var cardNumber = 0
    var shieldNumber = 0
    var bombNumber = 0

    @Bindable
    var magicCount = "×" + magicNumber.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.magicCount)
        }

    @Bindable
    var bombCount = "×" + bombNumber.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.bombCount)
        }

    @Bindable
    var cardCount = "×" + cardNumber.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardCount)
        }

    @Bindable
    var shieldCount = "×" + shieldNumber.toString()
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
                    /*magicNumber = it[0].count
                    bombNumber = it[1].count
                    cardNumber = it[2].count
                    shieldNumber = it[3].count*/
                }
    }

    interface Callback {

    }
}