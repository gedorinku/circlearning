package com.kurume_nct.studybattle.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.kurume_nct.studybattle.R
import android.support.annotation.RawRes



/**
 * Created by hanah on 9/24/2017.
 */
class UsersObject : Application(){

    var user: User = User()
    var nowGroup : Group = Group()
    var itemCount : HunachiItem = HunachiItem()
    private lateinit var prefer: SharedPreferences
    val myGroupCount: Int
        get() = myGroupList.size
    var myGroupList: MutableList<Group> = mutableListOf()
    var authenticationKey = "0"

    override fun onCreate() {
        super.onCreate()
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        authenticationKey = prefer.getString("key", "0")
    }

    fun writeFile(){
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        prefer.edit().apply {
            putString("key", authenticationKey)
            commit()
        }
    }

    fun deleteFile(){
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        authenticationKey = "0"
        prefer.edit().clear().commit()
    }

}