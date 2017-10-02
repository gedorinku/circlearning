package com.kurume_nct.studybattle.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import android.support.annotation.RawRes



/**
 * Created by hanah on 9/24/2017.
 */
class UnitPersonal : Application(){

    //書き直せるところはこのglobal変数を使うように書き換える


    var userName : String
    lateinit var userIcon : Uri
    var newUser : Boolean
    var nowGroup : Int
    var itemCount : MutableList<Int>
    private lateinit var prefer: SharedPreferences
    var groupCount : Int
    lateinit var autheticationKey: String

    init {
        userName = ""
        newUser = true
        nowGroup = 0
        groupCount = 6
        itemCount = mutableListOf(0,0,0)
    }

    override fun onCreate() {
        super.onCreate()
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        userName = prefer.getString("userName", "?????")
        if(userName != "?????"){
            newUser = false
        }
        userIcon = getIconUri(R.drawable.group)
    }

    fun getIconUri(@RawRes resId: Int) = Uri.parse("android.resource://$packageName/$resId")

    fun writeFile(){
        //add login_key too
        val edit = prefer.edit()
        edit.putString("userName",userName)
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