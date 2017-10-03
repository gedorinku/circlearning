package com.kurume_nct.studybattle.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.kurume_nct.studybattle.R
import android.support.annotation.RawRes



/**
 * Created by hanah on 9/24/2017.
 */
class UnitPersonal : Application(){

    var userName : String
    var userIcon : Uri
    var userIconId: Int
    var nowGroup : Int
    var itemCount : MutableList<Int>
    private lateinit var prefer: SharedPreferences
    var groupCount : Int
    var authenticationKey: String = "0"

    init {
        userName = ""
        nowGroup = 0
        groupCount = 6
        itemCount = mutableListOf(0,0,0,0)
        userIcon = getIconUri(R.drawable.group)
        userIconId = 0
    }

    override fun onCreate() {
        super.onCreate()
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        authenticationKey = prefer.getString("key", "0")
    }

    private fun getIconUri(@RawRes resId: Int) = Uri.parse("android.resource://$packageName/$resId")!!

    fun writeFile(){
        val edit = prefer.edit()
        edit.putString("key", authenticationKey)
        edit.commit()
    }

    fun deleteFile(){
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        prefer.edit().clear()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}