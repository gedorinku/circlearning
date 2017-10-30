package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.Toast
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ToolClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/25/2017.
 */
class ItemInfoViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    val unitPer: UnitPersonal = context.applicationContext as UnitPersonal

    @Bindable
    var magicCount = "×0"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.magicCount)
        }

    @Bindable
    var bombCount = "×0"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.bombCount)
        }

    @Bindable
    var cardCount = "×0"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardCount)
        }

    @Bindable
    var shieldCount = "×0"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.shieldCount)
        }

    fun onCreate() {
        ServerClient(unitPer.authenticationKey)
                .getMyItems(unitPer.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.forEach {
                        when (it.id) {
                            1 -> bombCount = "×" + it.count.toString()
                            2 -> shieldCount = "×" + it.count.toString()
                            3 -> cardCount = "×" + it.count.toString()
                            4 -> magicCount = "×" + it.count.toString()
                        }
                    }
                }
    }

    interface Callback {
        //none
    }
}