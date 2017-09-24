package com.kurume_nct.studybattle.`object`

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem

/**
 * Created by hanah on 9/24/2017.
 */
class UnitPersonal : Application(){

    //書き直せるところはこのglobal変数を使うように書き換える

    var userName : String
    var newUser : Boolean
    var nowGroup : Int
    private lateinit var prefer: SharedPreferences

    init {
        userName = ""
        newUser = true
        nowGroup = 0
    }

    override fun onCreate() {
        super.onCreate()
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        userName = prefer.getString("userName", "?????")
        if(userName == "?????"){
            newUser = false
        }

    }

    fun writeFile(){
        //add login_key too
        val edit = prefer.edit()
        edit.putString("userName",userName).commit()
    }

    fun deleteFile(){
        prefer = getSharedPreferences(packageName + ".txt", Context.MODE_PRIVATE)
        prefer.edit().clear()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}