package com.kurume_nct.studybattle.view

import com.kurume_nct.studybattle.R

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.*
import com.kurume_nct.studybattle.model.Air
import com.kurume_nct.studybattle.model.Bomb
import com.kurume_nct.studybattle.model.ProblemOpenAction
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ImageViewActivity
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.tools.ToolClass
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class CameraModeActivity : Activity() {

    private val RESULT_CAMERA = 1001
    private val RESULT_PICK_IMAGEFILE = 1000
    private val REQUEST_PERMISSION = 1002
    private lateinit var submitImageButton: ImageButton
    private lateinit var submitItemImageButton: ImageButton
    private var bmp1: Bitmap? = null
    private var cameraFile: File? = null
    private var cameraUri: Uri? = null
    private var problemUrl = ""
    private var filePath: String? = null
    private lateinit var dialog: AlertDialog
    private var putItemId = -1
    private lateinit var unitPer: UnitPersonal
    private var answerUri: Uri? = null
    private var problemId = 0
    private lateinit var submissionButton: Button
    private lateinit var passButton: Button
    lateinit var progress: ProgressDialog
    lateinit var dialogView: DialogItemSelectBinding
    private lateinit var problemImage: ImageView
    private lateinit var problemName: TextView
    private lateinit var writerName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unitPer = application as UnitPersonal
        progress = ProgressDialogTool(this).makeDialog()

        setContentView(R.layout.activity_camera_mode)
        // 宣言
        submitImageButton = findViewById(R.id.submit_image_button) as ImageButton
        submitItemImageButton = findViewById(R.id.submit_item_image_button) as ImageButton
        submissionButton = findViewById(R.id.submission_button) as Button
        passButton = findViewById(R.id.pass_button) as Button
        problemImage = findViewById(R.id.problem_image_at_camera) as ImageView
        problemName = findViewById(R.id.problem_name_at_camera) as TextView
        writerName = findViewById(R.id.writer_name_at_camera) as TextView

        problemId = intent.getIntExtra("problemId", 0)
        if (problemId == 0) {
            Toast.makeText(this, "やり直してください", Toast.LENGTH_SHORT).show()
            finish()
        }

        problemImage.apply {
            setOnClickListener {
                val intent = Intent(context, ImageViewActivity::class.java)
                intent.putExtra("url", problemUrl)
                startActivity(intent)
            }
            isClickable = false
        }

        openProblemServer()

        Glide.with(this).load(R.drawable.hatena).into(submitItemImageButton)
        onGetProblemInfo()

        //(uriについての実験機能)
        if (savedInstanceState != null) {
            cameraUri = savedInstanceState.getParcelable("CaptureUri")
        }

        submitImageButton.setOnClickListener {
            imageSetting()
        }

        submitItemImageButton.setOnClickListener {
            itemSetting()
        }

        //パスするボタン
        passButton.setOnClickListener {
            sadDialog()
        }

        //提出するボタン
        submissionButton.setOnClickListener {
            if (answerUri == null) {
                Toast.makeText(this, "写真を追加してください", Toast.LENGTH_SHORT).show()
            } else {
                sendProblemServer()
            }
        }

        dialogView = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_item_select, null, false)

        getItemData()

        unitPer.itemCount.run {
            if (bomb <= 0) dialogView.bombButton17.visibility = View.INVISIBLE
            if (card <= 0) dialogView.cardButton16.visibility = View.INVISIBLE
            if (magicHand <= 0) dialogView.handButton12.visibility = View.INVISIBLE
        }

        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()

        dialogView.bombButton17.setOnClickListener {
            if (putItemId != 0) {
                Glide.with(this).load(R.drawable.framecardb).into(submitItemImageButton)
            }
            putItemId = 0
            dialog.dismiss()
        }
        dialogView.cardButton16.setOnClickListener {
            if (putItemId != 1) {
                Glide.with(this).load(R.drawable.framecardc).into(submitItemImageButton)
            }
            putItemId = 1
            dialog.dismiss()
        }
        dialogView.handButton12.setOnClickListener {
            if (putItemId != 3) {
                Glide.with(this).load(R.drawable.framecardm).into(submitItemImageButton)
            }
            putItemId = 3
            dialog.dismiss()
        }
        dialogView.removeItemButton19.setOnClickListener {
            if (putItemId != -1) {
                Glide.with(this).load(R.drawable.hatena).into(submitItemImageButton)
            }
            putItemId = -1
            dialog.dismiss()
        }
    }

    private fun onGetProblemInfo() {
        val progressDialog = ProgressDialogTool(this).makeDialog()
        progressDialog.show()
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    problemName.text = it.title
                    client
                            .getUser(it.ownerId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                writerName.text = it.displayName
                            }
                    client
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    problemUrl = it.url
                    setUpPicture(Uri.parse(problemUrl))
                    problemImage.isClickable = true
                    progressDialog.dismiss()
                }, {
                    it.printStackTrace()
                    progressDialog.dismiss()
                })
    }

    fun setUpPicture(uri: Uri) {
        Glide.with(this).load(uri).into(problemImage)
    }

    private fun sendProblemServer() {
        val client = ServerClient(unitPer.authenticationKey)
        val uri: Uri = answerUri!!

        progress.show()
        client
                .uploadImage(uri, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    val imageId = it.id
                    client
                            .createSolution(
                                    text = "事前提出むり。みんな鬱になっちゃう。",
                                    problemId = problemId,
                                    imageIds = listOf(imageId),
                                    item =
                                    when (putItemId) {
                                        0 -> {
                                            Bomb
                                        }
                                        1 -> {
                                            Bomb/*Card*/
                                        }
                                        2 -> {
                                            Air/*Magic*/
                                        }
                                        else -> {
                                            Air
                                        }
                                    }
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }.subscribe({
            progress.dismiss()
            startActivity(Intent(this, LotteryActivity::class.java))
            finish()
        }, {
            progress.dismiss()
            it.printStackTrace()
            Toast.makeText(this, "解答提出に失敗しました。ネット環境を確認してください。", Toast.LENGTH_SHORT).show()
        })
    }

    private fun getItemData() {
        ToolClass(this).onRefreshItemData()
    }

    private fun sadDialog() {
        //send data📩
        ServerClient(unitPer.authenticationKey)
                .passProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val passAlert = AlertDialog.Builder(this)
                    val passView = this.layoutInflater.inflate(R.layout.dialog_pass_sad, null)
                    passView.setOnClickListener {
                        finish()
                    }
                    passAlert.setOnDismissListener {
                        finish()
                    }
                    passAlert.setView(passView)
                    val alert = passAlert.create()
                    alert.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alert.show()
                }
    }

    private fun imageSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        var dialog_: AlertDialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()
        dialogView.run {
            cameraButton.setOnClickListener {
                dialog_.cancel()
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission()
                } else {
                    cameraIntent()
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
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
            }
        }
        dialog_ = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()

        dialog_.show()
    }

    private fun itemSetting() {
        dialog.show()
    }


    //キャプチャーパス取得関数
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("CaptureUri", cameraUri)
    }

    //画像関連の処理関数
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CAMERA) {
            //カメラ撮影の処理
            if (cameraUri != null) {
                submitImageButton.setImageURI(cameraUri)
                registerDatabase(filePath)
            } else {
                Log.d("debug", "cameraUri == null")
            }

        } else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            //ギャラリーからの写真を選択
            var uri: Uri? = null
            if (data != null) {
                uri = data.data
                Log.i("", "Uri: " + uri!!.toString())
                answerUri = uri

                try {
                    bmp1 = getBitmapFromUri(uri)
                    submitImageButton.setImageBitmap(bmp1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        }
    }

    //カメラ撮影した際の画像を保存するフォルダ作成関数
    private fun cameraIntent() {
        // 保存先のフォルダーを作成
        val cameraFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ""
        )
        cameraFolder.mkdirs()

        // 保存ファイル名
        val fileName = SimpleDateFormat("ddHHmmss").format(Date())
        filePath = cameraFolder.path + "/" + fileName + ".jpg"
        Log.d("debug", "filePath:" + filePath!!)


        // capture画像のファイルパス
        cameraFile = File(filePath!!)
        //        cameraUri = Uri.fromFile(cameraFile);
        cameraUri = FileProvider.getUriForFile(this@CameraModeActivity, applicationContext.packageName + ".provider", cameraFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(intent, RESULT_CAMERA)
    }

    //画像をギャラリーから選んで取得して返す？（多分）
    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    //パーミッション確認関数
    private fun checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            cameraIntent()
        } else {
            requestLocationPermission()
        }// 拒否していた場合
    }

    // 許可を求める関数
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this@CameraModeActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)

        } else {
            val toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT)
            toast.show()

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)

        }
    }


    // 結果の受け取りする関数
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent()
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this, "カメラを使用するには許可が必要です", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    // アンドロイドのデータベースへ登録する関数
    private fun registerDatabase(file: String?) {
        val contentValues = ContentValues()
        val contentResolver = this@CameraModeActivity.contentResolver
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put("_data", file)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private var actionSignal = ProblemOpenAction.NONE
    private fun onBombDialog() {
        val dialog1 = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.bomb)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setOnClickListener {
                dialog1.cancel()
            }
        }

        dialog1.run {
            setContentView(image)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setOnDismissListener {
                when (actionSignal) {
                    ProblemOpenAction.EXPLODED -> {
                        onExpandDialog()
                    }
                    ProblemOpenAction.DEFENDED -> {
                        onShieldDialog()
                    }
                    else -> {
                        Log.ERROR
                    }
                }
            }
            show()
        }
    }

    private fun onShieldDialog() {
        val dialog1 = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.shield)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            background
            setOnClickListener {
                dialog1.cancel()
            }
        }
        dialog1.run {
            setContentView(image)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun onExpandDialog() {
        val dialog1 = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.bomb_second)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setOnClickListener {
                dialog1.cancel()
            }
        }
        dialog1.run {
            setContentView(image)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

    }

    private fun openProblemServer() {
        ServerClient(unitPer.authenticationKey)
                .openProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    actionSignal = it.openAction
                    when (actionSignal) {
                        ProblemOpenAction.NONE -> {
                            Toast.makeText(this, "爆弾はついてません", Toast.LENGTH_SHORT)
                        }
                        else -> {
                            onBombDialog()
                        }
                    }

                }
    }

}



