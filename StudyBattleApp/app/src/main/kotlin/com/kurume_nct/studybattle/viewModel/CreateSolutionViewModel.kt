package com.kurume_nct.studybattle.viewModel

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.databinding.DialogItemSelectBinding
import com.kurume_nct.studybattle.model.*
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.view.CreateSolutionActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * 🍣 Created by hanah on 2017/12/02.
 */
class CreateSolutionViewModel(val context: CreateSolutionActivity, val callback: Callback) : BaseObservable() {

    private val RESULT_PICK_IMAGEFILE = 1000
    lateinit var dialogView: DialogItemSelectBinding
    private lateinit var usersObject: UsersObject
    private lateinit var dialog: AlertDialog
    private var putItemId = 0

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setImage(imageView: ImageView, uri: Uri?) {
            if (uri == null) {
                Glide.with(imageView).load(R.drawable.plus).into(imageView)
            } else {
                Glide.with(imageView).load(uri).into(imageView)
            }
        }
    }

    @Bindable
    var problemName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var timeRemaining = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.timeRemaining)
        }

    @Bindable
    var writer = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.writer)
        }

    @Bindable
    var problemUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUri)
        }

    @Bindable
    var answerUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUri)
        }

    @Bindable
    var answerImageClickable = false
        set(value) {
            field = value
        }

    fun onClickAnswerImage(view: View) {
        imageSetting()
    }

    @Bindable
    var itemImageUri: Uri? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.itemImageUri)
        }

    @Bindable
    var itemImageClickable = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.itemImageClickable)
        }

    fun onClickItemImage(view: View) {
        dialog.show()
    }

    fun onClickSubmit(view: View) {
        if (answerUri != null) {
            Toast.makeText(context, "写真を追加してください", Toast.LENGTH_SHORT).show()
        } else {
            sendAnswerServer()
        }
    }

    @Bindable
    var submitable = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.submitable)
        }

    fun onClickPass(view: View) {
        onSadDialog()
    }

    fun onCreateView() {
        usersObject = context.applicationContext as UsersObject
        val progressDialog = ProgressDialogTool(context).makeDialog()
        progressDialog.show()
        val client = ServerClient(usersObject.authenticationKey)
        client
                .getProblem(callback.onGetProblemId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    problemName = it.title
                    val duration = Duration(it.assignedAtTime.toDateTime(), DateTime.now())
                    val hour = it.durationPerUser.standardHours - duration.standardHours
                    val min = (it.durationPerUser.standardMinutes - duration.standardMinutes) % 60
                    timeRemaining = hour.toString() + "時間 " + min.toString() + "分"
                    client
                            .getUser(it.ownerId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                writer = it.displayName
                            }
                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    problemUri = Uri.parse(it.url)
                    progressDialog.dismiss()
                    answerImageClickable = true
                    submitable = true
                }, {
                    it.printStackTrace()
                    progressDialog.dismiss()
                })
        dialogSetting()
    }

    private fun dialogSetting() {
        dialogView = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_item_select, null, false)

        getItemData()

        dialog = AlertDialog.Builder(context)
                .setView(dialogView.root)
                .create()

        dialogView.bombButton17.setOnClickListener {
            if (putItemId != 0) {
                itemImageUri = convertUrlFromDrawableResId(R.drawable.framecardb)
            }
            putItemId = 0
            dialog.dismiss()
        }
        dialogView.cardButton16.setOnClickListener {
            if (putItemId != 1) {
                itemImageUri = convertUrlFromDrawableResId(R.drawable.framecardc)
            }
            putItemId = 1
            dialog.dismiss()
        }
        dialogView.handButton12.setOnClickListener {
            if (putItemId != 2) {
                itemImageUri = convertUrlFromDrawableResId(R.drawable.framecardm)
            }
            putItemId = 2
            dialog.dismiss()
        }
        dialogView.removeItemButton19.setOnClickListener {
            if (putItemId != -1) {
                itemImageUri = convertUrlFromDrawableResId(R.drawable.hatena)
            }
            putItemId = -1
            dialog.dismiss()
        }
    }

    private fun getItemData() {
        ServerClient(usersObject.authenticationKey)
                .getMyItems(usersObject.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.forEach {
                        when (it.id) {
                            1 -> usersObject.itemCount.bomb = it.count
                            2 -> usersObject.itemCount.shield = it.count
                            3 -> usersObject.itemCount.card = it.count
                            4 -> usersObject.itemCount.magicHand = it.count
                        }
                    }
                    usersObject.itemCount.run {
                        if (bomb <= 0) dialogView.bombButton17.visibility = View.INVISIBLE
                        if (card <= 0) dialogView.cardButton16.visibility = View.INVISIBLE
                        if (magicHand <= 0) dialogView.handButton12.visibility = View.INVISIBLE
                    }
                }
    }


    private fun imageSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_camera_strage_choose, null, false
        )
        var dialog_: AlertDialog = AlertDialog.Builder(context)
                .setView(dialogView.root)
                .create()
        dialogView.run {
            cameraButton.setOnClickListener {
                dialog_.cancel()
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission()
                } else {
                    callback.cameraIntent()
                }
            }
            strageButton.setOnClickListener {
                dialog_.cancel()
                //ファイルを選択
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                //開けるものだけ表示
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                //イメージのみを表示するフィルタ
                intent.type = "image/*"
                callback.startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
            }
        }
        dialog_ = AlertDialog.Builder(context)
                .setView(dialogView.root)
                .create()

        dialog_.show()
    }

    private fun onSadDialog() {
        //send data📩
        ServerClient(usersObject.authenticationKey)
                .passProblem(callback.onGetProblemId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.onSadDialog()
                }
    }

    //パーミッション確認関数
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            callback.cameraIntent()
        } else {
            callback.requestLocationPermission()
        }
    }

    private fun sendAnswerServer() {
        val progress = ProgressDialog(context)
        val client = ServerClient(usersObject.authenticationKey)
        var randomItem: Int

        progress.show()
        client
                .uploadImage(answerUri!!, context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    val imageId = it.id
                    client
                            .createSolution(
                                    text = "解答提出",
                                    problemId = callback.onGetProblemId(),
                                    imageIds = listOf(imageId),
                                    item =
                                    when (putItemId) {
                                        0 -> {
                                            Bomb
                                        }
                                        1 -> {
                                            DoubleScoreCard
                                        }
                                        2 -> {
                                            MagicHand
                                        }
                                        else -> {
                                            Air
                                        }
                                    }
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    randomItem = it.receivedItemId
                    Log.d(randomItem.toString(), "アイテムの種類")
                    progress.dismiss()
                    callback.startLotteryActivity(randomItem)
                }, {
                    progress.dismiss()
                    it.printStackTrace()
                    Toast.makeText(context, "解答提出に失敗しました。ネット環境を確認してください。", Toast.LENGTH_SHORT).show()
                })
    }

    fun convertUrlFromDrawableResId(drawableResId: Int): Uri {
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
        fun onClickProblemImage()
        fun onGetProblemId(): Int
        fun startActivityForResult(intent: Intent, requestId: Int)
        fun onSadDialog()
        fun cameraIntent()
        fun requestLocationPermission()
        fun startLotteryActivity(random: Int)
    }
}