package com.kurume_nct.studybattle.`object`

import android.app.Application
import okio.Utf8
import java.io.File
import java.io.FileDescriptor.out

/**
 * Created by hanah on 9/24/2017.
 */
class UnitPersonal : Application(){

    val file = File(packageName + "_userName.txt")
    var userName : String
    var newUser : Boolean

    init {
        userName = ""
        newUser = true
    }

    override fun onCreate() {
        super.onCreate()
        file.canWrite()
        file.canRead()
        var name : String? = null
    }

    fun writeFile(){

    }

    fun deleteFile(){

    }

    override fun onTerminate() {
        super.onTerminate()
        userName = "Fin"
    }
}