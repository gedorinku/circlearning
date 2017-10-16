package com.kurume_nct.studybattle.tools

import android.app.ProgressDialog
import android.content.Context
import android.media.tv.TvContract
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 10/4/2017.
 */
class ProgressDialogTool(val context: Context) {
    fun makeDialog() : ProgressDialog{
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("少々お待ちください！")
        progressDialog.setTitle("サーバーと通信中⏰")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        return progressDialog
    }
}