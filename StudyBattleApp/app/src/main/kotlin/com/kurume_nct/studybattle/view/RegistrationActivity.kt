package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityRegistrationBinding
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity(), RegistrationViewModel.Callback {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var unitPer: UnitPersonal
    lateinit var progress: ProgressDialog
    lateinit var dialog: AlertDialog
    private val STORAGE_CODE = 1
    private val CAMERA_STORAGE_CODE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.userEntity = RegistrationViewModel(this, this)
        unitPer = application as UnitPersonal
        progress = ProgressDialogTool(this).makeDialog()

        dialogSetting()

        //skip not beginner
        if (unitPer.authenticationKey != "0") {
            onLogin()
        }
    }

    override fun toLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        enableButton(false)
        binding.userEntity.onActivityResult(data)
    }

    override fun stopButton(enable: Boolean) {
        if (enable) progress.show()
        binding.button3.isClickable = false
        binding.button4.isClickable = false
    }

    override fun enableButton(enable: Boolean) {
        if (enable) progress.dismiss()
        binding.button3.isClickable = true
        binding.button4.isClickable = true
    }

    override fun alertDialog() {
        stopButton(false)
        dialog.show()
    }

    private fun dialogSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        dialogView.run {
            cameraButton.setOnClickListener {
                cameraBeforeCheck()
                dialog.dismiss()
            }
            strageButton.setOnClickListener {
                storageBeforeCheck()
                dialog.dismiss()
            }
        }
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()
    }

    private fun storageBeforeCheck() {
        val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        when (permission) {
            PackageManager.PERMISSION_GRANTED -> {
                //storage
                storageStart()
            }
            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_CODE
                )
            }
        }
    }

    private fun cameraBeforeCheck() {
        val yet = arrayListOf(false, false)
        val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        )
        if (permission == PackageManager.PERMISSION_DENIED) {
            yet[0] = true
        }
        val permission2 = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission2 == PackageManager.PERMISSION_DENIED) {
            yet[1] = true
        }
        if (yet.contains(true)) {
            when {
                yet.all { it } -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA),
                        CAMERA_STORAGE_CODE
                )
                yet[0] -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        CAMERA_STORAGE_CODE
                )
                else -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CAMERA_STORAGE_CODE
                )
            }
        } else {
            //camera
            cameraStart()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    cameraStart()
                } else {
                    //back to deselect
                    enableButton(false)
                    Toast.makeText(this, "カメラを起動するためには許可が必要です", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storageStart()
                } else {
                    //back to deselect
                    enableButton(false)
                    Toast.makeText(this, "写真を参照するには許可が必要です", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cameraStart() {
        //camera
        val photoName = System.currentTimeMillis().toString() + ".jpg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, photoName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 0)
    }

    private fun storageStart() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, 0)
    }
}
