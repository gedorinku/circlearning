package com.kurume_nct.studybattle.view

import com.kurume_nct.studybattle.R

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.kurume_nct.studybattle.model.*
import com.kurume_nct.studybattle.tools.ImageViewActivity
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.viewModel.CreateSolutionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import java.io.File
import java.io.IOException

//by kirby and hunachi, converted to kotlin by hunachi

class CreateSolutionActivity : Activity(), CreateSolutionViewModel.Callback {

    private val RESULT_CAMERA = 1001
    private val RESULT_PICK_IMAGEFILE = 1000
    private val REQUEST_PERMISSION = 1002
    private var problemUrl = ""
    private var filePath: String? = null
    private lateinit var dialog: AlertDialog
    private lateinit var unitPer: UnitPersonal
    private var problemId = 0

    lateinit var binding: ActivityCreateSolutionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unitPer = application as UnitPersonal

        // 宣言
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_solution)

        problemId = intent.getIntExtra("problemId", 0)
        if (problemId == 0) {
            Toast.makeText(this, "やり直してください", Toast.LENGTH_SHORT).show()
            finish()
        }
        openProblemServer()
        binding.viewModel.onCreateView()
    }

    override fun onClickProblemImage() {
        val intent = Intent(this, ImageViewActivity::class.java)
        intent.putExtra("url", problemUrl)
        startActivity(intent)
    }


    override fun onSadDialog() {
        val passAlert = AlertDialog.Builder(this)
        val passView = layoutInflater.inflate(R.layout.dialog_pass_sad, null)
        passView.setOnClickListener {
            setResult(0)
            finish()
        }
        passAlert.setOnDismissListener {
            setResult(0)
            finish()
        }
        passAlert.setView(passView)
        val alert = passAlert.create()
        alert.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    //画像関連の処理関数
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CAMERA) {
            //カメラ撮影の処理
            binding.viewModel.answerUri = data?.data
            registerDatabase(filePath)
        } else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            //ギャラリーからの写真を選択
            binding.viewModel.answerUri = data?.data
        }
    }

    //カメラ撮影した際の画像を保存するフォルダ作成関数
    override fun cameraIntent() {
        // 保存先のフォルダーを作成
        val cameraFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ""
        )
        cameraFolder.mkdirs()

        // 保存ファイル名
        val fileName = DateTimeFormat.forPattern("ddHHmmss").print(DateTime.now())
        filePath = cameraFolder.path + "/" + fileName + ".jpg"
        Log.d("debug", "filePath:" + filePath!!)

        val cameraUri = FileProvider.getUriForFile(
                this@CreateSolutionActivity,
                applicationContext.packageName + ".provider",
                File(filePath!!)
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        binding.viewModel.answerUri = cameraUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(intent, RESULT_CAMERA)
    }


    // 許可を求める関数
    override fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this@CreateSolutionActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            Toast.makeText(this, "許可して♡", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
            )
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
        val contentResolver = this@CreateSolutionActivity.contentResolver
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put("_data", file)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private var actionSignal = ProblemOpenAction.NONE

    private fun onBombDialog() {
        val dialog = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.bomb)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setOnClickListener {
                dialog.cancel()
            }
        }
        dialog.run {
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
        val dialog = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.shield)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            background
            setOnClickListener {
                dialog.cancel()
            }
        }
        dialog.run {
            setContentView(image)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun onExpandDialog() {
        val dialog = Dialog(this)
        val image = ImageView(this)
        image.run {
            setImageResource(R.drawable.bomb_second)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setOnClickListener {
                dialog.cancel()
            }
        }
        dialog.run {
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
                .subscribe({
                    actionSignal = it.openAction
                    when (actionSignal) {
                        ProblemOpenAction.NONE -> {
                            Log.d("爆弾はついてません", "at make solution.")
                        }
                        else -> {
                            onBombDialog()
                        }
                    }

                }, {
                    Toast.makeText(this, "bombがついているかわかりませんでした", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                })
    }

    override fun onGetProblemId() = problemId

    override fun startLotteryActivity(randomItem: Int) {
        val intent = Intent(this, LotteryActivity::class.java)
        intent.putExtra("item", randomItem)
        startActivity(intent)
        setResult(0)
        finish()
    }

}



