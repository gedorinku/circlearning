package com.kurume_nct.studybattle.view

import com.kurume_nct.studybattle.R

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.content.Intent
import android.databinding.DataBindingUtil
import android.widget.ImageButton
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.databinding.DialogItemSelectBinding
import com.kurume_nct.studybattle.model.Item
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class CameraModeActivity : Activity() {

    private lateinit var submitImageButton: ImageButton
    private lateinit var submitItemImageButton: ImageButton
    private var libraryButton: Button? = null
    private var comment: TextView? = null
    private var experiment: TextView? = null//これで実験試してる
    private var flag = 0
    private var userName: String? = null
    private var bmp1: Bitmap? = null
    private var cameraFile: File? = null
    private var cameraUri: Uri? = null
    private var filePath: String? = null
    private lateinit var dialog: AlertDialog
    private var putItemId = -1
    private lateinit var unitPer: UnitPersonal
    private var answerUri: Uri? = null
    //getExtraか何かでもらう
    private var problemId = 0
    private lateinit var submissionButton: Button
    private lateinit var passButton: Button
    lateinit var progress: ProgressDialog
    lateinit var dialogView: DialogItemSelectBinding


    //ギャラリーpath取得関数
    private val galleryPath: String
        get() = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM + "/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        unitPer = application as UnitPersonal
        progress = ProgressDialogTool(this).makeDialog()

        setContentView(R.layout.activity_camera_mode)
        userName = intent.getStringExtra("userName")
        // 宣言
        comment = findViewById(R.id.comment) as TextView
        submitImageButton = findViewById(R.id.submit_image_button) as ImageButton
        submitItemImageButton = findViewById(R.id.submit_item_image_button) as ImageButton
        //libraryButton = findViewById(R.id.library_button) as Button
        experiment = findViewById(R.id.experiment) as TextView
        submissionButton = findViewById(R.id.submission_button) as Button
        passButton = findViewById(R.id.pass_button) as Button
        //Glide.with(this).load(R.drawable.hatena).into(submitItemImageButton)


        problemId = intent.getIntExtra("problemId", 0)

        Glide.with(this).load(R.drawable.hatena).into(submitItemImageButton)

        //(uriについての実験機能)
        if (savedInstanceState != null) {
            cameraUri = savedInstanceState.getParcelable("CaptureUri")
        }


       /* libraryButton!!.setOnClickListener {
            //ファイルを選択
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            //開けるものだけ表示
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //イメージのみを表示するフィルタ
            intent.type = "image*//*"
            startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
        }*/

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
            /*if (flag == 1) {
                val intent1 = Intent(application, LotteryActivity::class.java)
                intent1.putExtra("userName", userName)
                startActivity(intent1)
                finish()
            } else
                comment!!.text = "解答を提出してください"*/
            if (answerUri == null) {
                Toast.makeText(this, "写真を追加してください", Toast.LENGTH_SHORT).show()
            } else {
                sendProblemServer()
            }
        }

        //toClickableButton()
        dialogView = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_item_select, null, false)

        unitPer.itemCount.run {
            if (bomb <= 0) dialogView.bombButton17.visibility = View.INVISIBLE
            if (card <= 0) dialogView.cardButton16.visibility = View.INVISIBLE
            if (magicHand <= 0) dialogView.handButton12.visibility = View.INVISIBLE
        }

        dialogView.bombButton17.setOnClickListener {
            if (putItemId != 0) {
                Glide.with(this).load(R.drawable.framecard_bomb).into(submitItemImageButton)
            }
            putItemId = 0
            dialog.cancel()
        }
        dialogView.cardButton16.setOnClickListener {
            if (putItemId != 1) {
                Glide.with(this).load(R.drawable.framecard_card).into(submitItemImageButton)
            }
            putItemId = 1
            dialog.cancel()
        }
        dialogView.handButton12.setOnClickListener {
            if (putItemId != 3) {
                Glide.with(this).load(R.drawable.framecard_magichand).into(submitItemImageButton)
            }
            putItemId = 3
            dialog.cancel()
        }
        dialogView.removeItemButton19.setOnClickListener {
            if (putItemId != -1) {
                Glide.with(this).load(R.drawable.hatena).into(submitItemImageButton)
                dialog.cancel()
            }
        }

    }

    private fun sendProblemServer() {
        val client = ServerClient(unitPer.authenticationKey)
        val uri: Uri = answerUri!!

        progress.show()
        client
                .uploadImage(uri, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val imageId = it.id
                    client
                            .createSolution(
                                    text = "事前提出むり。みんな鬱になっちゃう。",
                                    problemId = problemId,
                                    imageIds = listOf(imageId)
                                    //item = putImageId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                progress.dismiss()
                                decrementItem(putItemId)
                                startActivity(Intent(this, LotteryActivity::class.java))
                                finish()
                            }, {
                                progress.dismiss()
                                it.printStackTrace()
                                Toast.makeText(this, "解答提出に失敗しました。ネット環境を確認してください。", Toast.LENGTH_SHORT).show()
                            })
                }, {
                    progress.dismiss()
                    it.printStackTrace()
                    Toast.makeText(this, "解答提出に失敗しました。画像データが大きすぎる可能性があります。", Toast.LENGTH_SHORT).show()
                })
    }

    private fun sadDialog() {
        //send data📩
        val passAlert = AlertDialog.Builder(this)
        val passView = this.layoutInflater.inflate(R.layout.dialog_pass_sad, null)
        passAlert.setOnDismissListener {
            finish()
        }
        passAlert.setView(passView)
        val alert = passAlert.create()
        alert.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private fun imageSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        dialogView.run {
            cameraButton.setOnClickListener {
                dialog.cancel()
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission()
                } else {
                    cameraIntent()
                }
            }
            strageButton.setOnClickListener {
                dialog.cancel()
                //ファイルを選択
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                //開けるものだけ表示
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                //イメージのみを表示するフィルタ
                intent.type = "image/*"
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
            }
        }
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()

        dialog.show()
    }

    private fun itemSetting() {
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()

        dialog.show()
    }

    fun decrementItem(itemId: Int) {
        if (itemId != -1) {
            when (itemId) {
                0 -> unitPer.itemCount.bomb -= 1
                1 -> unitPer.itemCount.card -= 1
                3 -> unitPer.itemCount.magicHand -= 1
            }
        }
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
        flag = 1
        comment!!.text = "解答を提出してね"
        // experiment.setText(filePath);
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
                return
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT)
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

    companion object {
        var instance: CameraModeActivity? = null
            private set

        private val RESULT_CAMERA = 1001
        private val RESULT_PICK_IMAGEFILE = 1000
        private val REQUEST_PERMISSION = 1002

        //uriからpath取得
        fun getPath(context: Context, uri: Uri): String {
            val contentResolver = context.contentResolver
            val columns = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, columns, null, null, null)
            cursor!!.moveToFirst()
            val path = cursor.getString(0)
            cursor.close()
            return path
        }
    }

}



