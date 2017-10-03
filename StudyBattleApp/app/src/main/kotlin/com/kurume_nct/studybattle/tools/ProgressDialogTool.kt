package com.kurume_nct.studybattle.tools

import android.app.ProgressDialog
import android.content.Context
import android.media.tv.TvContract

/**
 * Created by hanah on 10/4/2017.
 */
class ProgressDialogTool(val context: Context) {
    fun makeDialog() : ProgressDialog{
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("少々お待ちください")
        progressDialog.setTitle("サーバーとの通信中です")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        return progressDialog
    }
}