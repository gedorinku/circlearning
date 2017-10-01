package com.kurume_nct.studybattle.tools

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

/**
 * Created by hanah on 9/30/2017.
 */
class ResIDToUriClass {
    fun convertUrlFromDrawableResId(context: Context, drawableResId: Int): Uri? {
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
}