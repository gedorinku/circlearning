package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.listFragment.SelectMainPeopleFragment
import com.kurume_nct.studybattle.model.UsersObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/1/2017.
 */
class CreateGroupViewModel(
        private val context: Context,
        private val callback: Callback,
        private val fragment: SelectMainPeopleFragment
) : BaseObservable() {

    val usersObject = context.applicationContext as UsersObject

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

    fun onClickMakeGroup(view: View) {
        callback.makeGroup()
    }

    fun createGroup() {
        //val toast = Toast.makeText(context, "グループ名が適切ではありません", Toast.LENGTH_LONG)
        val client = ServerClient(usersObject.authenticationKey)
        client
                .createGroup(groupName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    usersObject.myGroupList.add(it)
                    for (user in fragment.peopleList) {
                        client
                                .attachToGroup(it, user)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { Log.d(user.displayName, it.toString()) }
                    }
                    callback.onSuccess()
                }, {
                    it.printStackTrace()
                    callback.onError()
                })
    }

    interface Callback {
        fun makeGroup()
        fun onSuccess()
        fun onError()
    }
}