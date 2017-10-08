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

    //var userName : String
    var myInfomation: User = User()
    var userIcon : Uri
    var nowGroup : Group
    var itemCount : Item
    private lateinit var prefer: SharedPreferences
    var myGroupCount: Int
    var myGroupList: MutableList<Group>
    var authenticationKey: String = "0"

    init {
        nowGroup = Group()
        myGroupCount = 1
        itemCount = Item(1,1,1,1)
        userIcon = getIconUri(R.drawable.group)
        myGroupList = mutableListOf()
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
        prefer.edit().clear().commit()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}