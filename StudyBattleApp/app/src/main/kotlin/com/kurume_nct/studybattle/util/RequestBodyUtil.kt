package com.kurume_nct.studybattle.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio


/**
 * Created by gedorinku on 2017/11/01.
 */
/**
 * RequestBodyのファクトリ関数です。
 * RequestBodyはCompanion objectを持たないので、拡張関数にできるつらい
 */
fun createRequestBody(contentType: MediaType?, uri: Uri, context: Context): RequestBody {
    val projection = arrayOf(MediaStore.MediaColumns.SIZE)
    val length = context.contentResolver.query(uri, projection, null, null, null)?.use {
        if (it.moveToFirst()) {
            it.getLong(0)
        } else {
            null
        }
    } ?: throw IllegalArgumentException("残念ながらuriからファイルサイズを取得できません")

    return object : RequestBody() {

        override fun contentLength(): Long = length

        override fun contentType(): MediaType? = contentType

        override fun writeTo(sink: BufferedSink) {
            Okio.source(context.contentResolver.openInputStream(uri)).use {
                sink.writeAll(it)
            }
        }

    }
}
