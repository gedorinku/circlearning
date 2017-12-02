package com.kurume_nct.studybattle.viewModel

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.tools.ToolClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream

/**
 * Created by hanah on 7/30/2017.
 */
class RegistrationViewModel(private val context: Context, private val callback: Callback) : BaseObservable() {

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setIconImage(view: ImageView, uri: Uri?) {
            if(uri == null){
                Glide.with(view).load(R.drawable.plus).into(view)
            }
            Glide.with(view).load(uri).into(view)
        }
    }

    @Bindable
    var userNameRegister = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.userNameRegister)
        }

    @Bindable
    var userPassword = ""
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.userPassword)
        }

    @Bindable
    var displayName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.displayName)
        }

    @Bindable
    var loginButtonText = "登録"

    @Bindable
    var imageUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUri)
        }

    fun onClicktoLoginButton(view: View) {
        callback.toLoginActivity()
    }

    fun onClickLoginButton(view: View) {
        if (userNameRegister.isBlank() || userPassword.isBlank()) {
            Toast.makeText(context, context.getString(R.string.errorLoginStatus), Toast.LENGTH_LONG).show()
        } else if (!userNameRegister.matches("^[a-zA-Z0-9_]{2,20}".toRegex())) {
            Toast.makeText(context, "ユーザー名に不適切な文字が含まれています。", Toast.LENGTH_LONG).show()
            userNameRegister = ""
        } else if (imageUri == null) {
            Toast.makeText(context, "アイコン画像を選択してください。", Toast.LENGTH_LONG).show()
        } else {
            val progress = ProgressDialogTool(context).makeDialog()
            progress.show()
            callback.stopButton()
            //sever処理
            Log.d("開始", "Register")
            val client = ServerClient()
            client
                    .uploadImage(imageUri!!, context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //たまに落ちるので分けた。
                        Log.d("開始2", "Register")
                        client
                                .register(displayName, userNameRegister, userPassword, it.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    callback.onLogin()
                                    progress.dismiss()
                                    callback.enableButton()
                                }, {
                                    Toast.makeText(context, context.getString(R.string.usedUserNameAlart), Toast.LENGTH_LONG).show()
                                    progress.dismiss()
                                    callback.enableButton()
                                })
                    }, {
                        Toast.makeText(context, "もう一度やり直してください", Toast.LENGTH_LONG).show()
                        progress.dismiss()
                        callback.enableButton()
                    })
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        imageUri = data.data
        callback.enableButton()
    }

    fun onClickChangeIconImage(view: View) {
        callback.alertDialog()
    }

    fun changeImageSize(uri: Uri): Bitmap {
        val stream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(BufferedInputStream(stream))
        return Bitmap.createScaledBitmap(bitmap, 100, 100, false)
    }

    private fun convertUrlFromDrawableResId(context: Context, drawableResId: Int): Uri? {
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
        fun enableButton()
        fun stopButton()
        fun toLoginActivity()
        fun onLogin()
        fun startActivityForResult(intent: Intent, requestCode: Int)
        fun alertDialog()
    }

}