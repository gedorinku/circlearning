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
    var magicCount = "×" + unitPer.itemCount.magicHand.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.magicCount)
        }

    @Bindable
    var bombCount = "×" + unitPer.itemCount.bomb.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.bombCount)
        }

    @Bindable
    var cardCount = "×" + unitPer.itemCount.card.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.cardCount)
        }

    @Bindable
    var shieldCount = "×" + unitPer.itemCount.shield.toString()
        set(value) {
            field = value
            notifyPropertyChanged(BR.shieldCount)
        }

    fun onCreate(){
        ToolClass(context).onRefreshItemData()
    }

    interface Callback {
        //none
    }
}