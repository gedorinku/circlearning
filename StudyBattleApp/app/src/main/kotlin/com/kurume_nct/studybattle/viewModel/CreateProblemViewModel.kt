package com.kurume_nct.studybattle.viewModel

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import android.provider.MediaStore
import android.content.ContentValues
import android.widget.Toast


/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    private var pUri: Uri
    private var aUri: Uri
    private var checkCount: Boolean
    private var termOne: Double
    private val termExtra = "時間(解答回収期間より)"

    init {
        checkCount = false
        termOne = 24.0
        pUri = convertUrlFromDrawableResId(context, R.drawable.group)
        aUri = convertUrlFromDrawableResId(context, R.drawable.group)
    }

    companion object {
        @BindingAdapter("loadProblem")
        @JvmStatic
        fun setCreateImage(view: ImageView, uri: Uri?) {
            if (uri == null) {
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
    var day = "5日"
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.day)
        }

    fun onClickDateChange(view: View){
        callback.onDateDialog()
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


    //permission dialogをだす。
    fun onGetImage(camera: Int, pro: Int) {
        when (camera) {
            0 -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
                callback.startActivityForResult(intent, pro)
            }
            1 -> {
                //camera
                val photoName = System.currentTimeMillis().toString() + ".jpg"
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Images.Media.TITLE, photoName)
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                val uri = context.contentResolver
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                callback.startActivityForResult(intent, pro)
                //Toast.makeText(context, "shushi~!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onClickProblemImage(view: View) {
        callback.alertDialog(1)
        //using alert screen ans to do to choice photo or image
    }

    fun onClickAnswerImage(view: View) {
        callback.alertDialog(0)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (requestCode) {
            1 -> {
                pUri = data.data
                problemUri = pUri
            }
            0 -> {
                aUri = data.data
                answerUri = aUri
            }
        }
    }

    fun onClickFinish(view: View) {
        val a = convertUrlFromDrawableResId(context, R.drawable.group)
        if(problemUri == a || answerUri == a || problemName.isEmpty() || problemName.isBlank()) {
            Toast.makeText(context, "入力に不備があります(`・ω・´)",Toast.LENGTH_SHORT).show()
        }else{
            callback.getCreateData(problemName, problemUri!!, answerUri!!)
        }
    }

    fun convertUrlFromDrawableResId(context: Context, drawableResId: Int): Uri {
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
        fun getCreateData(title: String, problemUri: Uri, answerUri: Uri)
        fun alertDialog(pro: Int)
        fun onDateDialog()
    }
}