package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UsersObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 8/11/2017.
 */
class LoginViewModel(
        private val context: Context,
        private val callback: Callback
) : BaseObservable() {

    val userObject = context.applicationContext as UsersObject

    @Bindable
    var name = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @Bindable
    var password = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }


    fun onClickLogin(view: View) {
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.errorLoginStatus), Toast.LENGTH_LONG).show()
        } else {
            callback.stopButton()
            val client = ServerClient(userObject.authenticationKey)
            client
                    .login(name, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        callback.onLogin(it.authenticationKey)
                        callback.clickableButton()
                    }, {
                        Toast.makeText(context, "ログインに失敗しましたユーザー名とパスワードを確認してください.", Toast.LENGTH_SHORT).show()
                        callback.clickableButton()
                    })
        }
    }

    fun onClickToRegister(view: View) {
        callback.toRegisterActivity()
    }


    interface Callback {
        fun stopButton()
        fun clickableButton()
        fun onLogin(authentication: String)
        fun toRegisterActivity()
    }
}