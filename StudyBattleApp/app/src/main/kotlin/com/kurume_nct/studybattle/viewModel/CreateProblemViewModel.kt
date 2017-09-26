package com.kurume_nct.studybattle.viewModel

import android.content.ContentResolver
import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R

/**
 * Created by hanah on 9/26/2017.
 */
class CreateProblemViewModel(context: Context, private val callback: Callback) : BaseObservable() {

    private var _problemUri: Uri
    private var _answerUri: Uri
    private var checkCount: Boolean
    private var termOne : Double
    private val termExtra = "時間(解答回収期間より)"

    init {
        _problemUri = convertUrlFromDrawableResId(context, R.drawable.group)!!
        _answerUri = convertUrlFromDrawableResId(context, R.drawable.group)!!
        checkCount = false
        termOne = 24.0
    }

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setproblemImage(view: ImageView, uri: Uri) {
            when (view.id) {
                R.id.problemImage -> {
                    Glide.with(view).load(uri).into(view)
                }
                R.id.answerImage -> {
                    Glide.with(view).load(uri).into(view)
                }
            }

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
    var problemUri = _problemUri
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    @Bindable
    var answerUri = _answerUri
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

    }

    fun onClickAnswerImage(view: View) {

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
    }
}