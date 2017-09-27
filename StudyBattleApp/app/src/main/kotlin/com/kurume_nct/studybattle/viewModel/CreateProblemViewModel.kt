package com.kurume_nct.studybattle.viewModel

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    private var pUri: Uri
    private var aUri: Uri
    private var checkCount: Boolean
    private var termOne : Double
    private val termExtra = "時間(解答回収期間より)"

    init {
        pUri = convertUrlFromDrawableResId(context, R.drawable.group)!!
        aUri = convertUrlFromDrawableResId(context, R.drawable.group)!!
        checkCount = false
        termOne = 24.0
    }

    companion object {
        @BindingAdapter("loadProblem")
        @JvmStatic
        fun setCreateImage(view: ImageView, uri: Uri?) {
            if(uri == null){
                Glide.with(view).load(R.drawable.group).into(view)
            }
            Glide.with(view).load(uri).into(view)
        }
    }


    @Bindable
    var creatorName = ""

    @Bindable
    var problemName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var termForOne = termOne.toString() + termExtra

    @Bindable
    var problemUri = pUri
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    @Bindable
    var answerUri = aUri
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUri)
        }


    fun onClickCheckbox(view: View) {
        if (!checkCount) {
            callback.checkNameEnable(true)
        } else {
            callback.checkNameEnable(false)
        }
        checkCount = !checkCount
    }

    fun onClickProblemImage(view: View) {
        //using alert screen ans to do to choice photo or image
        when(callback.alertDialog()){
            0 -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
                callback.startActivityForResult(intent, 0)
            }
            1 ->{

            }
        }

    }

    fun onClickAnswerImage(view: View) {
        when(callback.alertDialog()){
            0 -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
                callback.startActivityForResult(intent, 1)
            }
            1 ->{

            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(data == null)return
        //Log.d("requestCode is " + requestCode.toString(),"resultCode is" + resultCode.toString())
        when(requestCode){
            0 -> {
                pUri = data.data
                problemUri = pUri
                callback.getProblemPhoto()
            }
            1 -> {
                aUri = data.data
                answerUri = aUri
                callback.getAnswerPhoto()
            }
        }
    }

    fun onClickFinish(view: View) {

    }

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

    interface Callback {

        fun checkNameEnable(enable: Boolean)
        fun startActivityForResult(intent: Intent, requestCode: Int)
        fun getProblemPhoto()
        fun getAnswerPhoto()
        fun alertDialog() : Int
    }
}