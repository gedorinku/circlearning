package com.kurume_nct.studybattle.view

import com.kurume_nct.studybattle.R

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Toast
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.databinding.DialogItemSelectBinding

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class CameraModeActivity : Activity() {

    private var imageview: ImageView? = null
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


    //ギャラリーpath取得関数
    private val galleryPath: String
        get() = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM + "/"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        instance = this

        setContentView(R.layout.activity_camera_mode)
        userName = intent.getStringExtra("userName")
        // 宣言
        comment = findViewById(R.id.comment) as TextView
        imageview = findViewById(R.id.answer) as ImageView
        libraryButton = findViewById(R.id.library_button) as Button
        experiment = findViewById(R.id.experiment) as TextView
        val submissionButton = findViewById(R.id.submission_button) as Button
        val passButton = findViewById(R.id.pass_button) as Button

        //add.
        val itemView = findViewById(R.id.item_image) as ImageButton

        itemView.setOnClickListener {

        }

        //(uriについての実験機能)
        if (savedInstanceState != null) {
            cameraUri = savedInstanceState.getParcelable("CaptureUri")
        }


        libraryButton!!.setOnClickListener {
            //ファイルを選択
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            //開けるものだけ表示
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //イメージのみを表示するフィルタ
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
        }

        //カメラ撮影ボタン
        val cameraButton = findViewById(R.id.camera_button) as Button
        cameraButton.setOnClickListener {
            // Android 6, API 23以上でパーミッシンの確認
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermission()
            } else {
                cameraIntent()
            }
        }

        //パスするボタン
        passButton.setOnClickListener {
            //他の画面に遷移しないようにする
            submissionButton.isEnabled = false
            cameraButton.isEnabled = false
            libraryButton!!.isEnabled = false


            val handler = Handler()
            handler.postDelayed({ finish() }, 2500)
        }

        //提出するボタン
        submissionButton.setOnClickListener {
            if (flag == 1) {
                val intent1 = Intent(application, LotteryActivity::class.java)
                intent1.putExtra("userName", userName)
                startActivity(intent1)
                finish()
            } else
                comment!!.text = "解答を提出してください"
        }
    }

    private fun imageSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        dialogView.run {
            cameraButton.setOnClickListener {
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission()
                } else {
                    cameraIntent()
                }
            }
            strageButton.setOnClickListener {
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

    fun itemSetting() {
        val dialogView : DialogItemSelectBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_item_select, null, false)
        dialogView.run {
            bombButton17.setOnClickListener {

            }
            cardButton16.setOnClickListener {

            }
            shieldButton15.setOnClickListener {

            }
            handButton12.setOnClickListener {

            }
        }
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()

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
                imageview!!.setImageURI(cameraUri)
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

                try {
                    bmp1 = getBitmapFromUri(uri)
                    imageview!!.setImageBitmap(bmp1)
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



