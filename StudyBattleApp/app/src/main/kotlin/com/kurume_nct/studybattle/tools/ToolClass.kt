package com.kurume_nct.studybattle.tools

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 9/30/2017.
 */
class ToolClass(val context: Context) {

    val unitPer: UnitPersonal = context.applicationContext as UnitPersonal

    fun convertUrlFromDrawableResId(drawableResId: Int): Uri? {
        val sb = StringBuilder()
        sb.append(ContentResolver.SCHEME_ANDROID_RESOURCE)
        sb.append("://")
        sb.append(context.resources.getResourcePackageName(drawableResId))
        sb.append("/")
        sb.append(context.resources.getResourceTypeName(drawableResId))
        sb.append("/")
        sb.append(context.resources.getResourceEntryName(drawableResId))
        return Uri.parse(sb.toString())
    }

    fun onRefreshItemData(){
        ServerClient(unitPer.authenticationKey)
                .getMyItems(unitPer.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.forEach {
                        when(it.id){
                            1 -> unitPer.itemCount.bomb = it.count
                            2 -> unitPer.itemCount.bomb = it.count
                        }
                    }
                }
    }
}